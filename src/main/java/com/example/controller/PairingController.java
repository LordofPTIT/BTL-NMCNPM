package com.example.controller;

import com.example.dao.*;
import com.example.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class PairingController {
    private TournamentDAO tournamentDAO;
    private RoundDAO roundDAO;
    private TournamentPlayerDAO tournamentPlayerDAO;
    private MatchDAO matchDAO;
    private ChessPlayerDAO chessPlayerDAO;


    public PairingController() {
        this.tournamentDAO = new TournamentDAO();
        this.roundDAO = new RoundDAO();
        this.tournamentPlayerDAO = new TournamentPlayerDAO();
        this.matchDAO = new MatchDAO();
        this.chessPlayerDAO = new ChessPlayerDAO();
    }

    public List<Tournament> getAllTournaments() {
        return tournamentDAO.getAllTournaments();
    }

    // UPDATED to pass through the call
    public List<Round> getCompletedRoundsByTournament(int tournamentId) {
        return roundDAO.getCompletedRoundsByTournament(tournamentId);
    }

    public int getNextRoundNumberBasedOnExisting(int tournamentId) {
        return roundDAO.getNextRoundNumberBasedOnExisting(tournamentId);
    }

    /**
     * Gets current standings.
     * @param tournamentId The ID of the tournament.
     * @param basedOnRoundNumber The round number AFTER which standings are required.
     * Pass 0 if standings are for Round 1 (based on initial Elo).
     * @return List of Standing objects.
     */
    public List<Standing> getCurrentStandings(int tournamentId, int basedOnRoundNumber) {
        List<TournamentPlayer> players;
        // If basedOnRoundNumber is 0, it means we're preparing for Round 1.
        // Standings should be based on initial Elo.
        if (basedOnRoundNumber == 0) {
            players = tournamentPlayerDAO.getTournamentPlayersForRound1Pairing(tournamentId);
        } else {
            // For subsequent rounds, standings are based on results after the 'basedOnRoundNumber'.
            // The DAO method already sorts by points, opponent points, current Elo.
            players = tournamentPlayerDAO.getTournamentPlayersByTournamentId(tournamentId);
        }

        List<Standing> standings = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            TournamentPlayer tp = players.get(i);
            ChessPlayer cp = tp.getPlayerDetails();
            if (cp == null) {
                cp = chessPlayerDAO.getPlayerById(tp.getPlayerId());
            }

            standings.add(new Standing(
                    i + 1, // rank
                    tp.getPlayerId(),
                    cp != null ? cp.getName() : "Unknown Player " + tp.getPlayerId(),
                    // Use initial Elo if it's for Round 1 (basedOnRoundNumber == 0), otherwise currentElo.
                    basedOnRoundNumber == 0 ? (cp != null ? cp.getInitialElo() : 0) : tp.getCurrentElo(),
                    tp.getTotalPoints(),
                    tp.getSumOpponentPoints(),
                    tp.getOpponentsPlayedIds(),
                    tp.hasReceivedBye()
            ));
        }
        return standings;
    }

    public List<Match> generatePairings(int tournamentId, int forRoundNumber, List<Standing> currentStandings) {
        List<Match> pairings = new ArrayList<>();
        List<TournamentPlayer> availablePlayers = currentStandings.stream()
                .map(s -> {
                    TournamentPlayer tp = new TournamentPlayer();
                    tp.setPlayerId(s.getPlayerId());
                    // Ensure playerDetails are loaded for name and Elo in pairing logic
                    ChessPlayer details = chessPlayerDAO.getPlayerById(s.getPlayerId());
                    tp.setPlayerDetails(details);
                    tp.setCurrentElo(s.getCurrentElo()); // This should be correct based on getCurrentStandings
                    tp.setTotalPoints(s.getTotalPoints());
                    tp.setSumOpponentPoints(s.getSumOpponentPoints());
                    tp.setOpponentsPlayedRaw(s.getOpponentsPlayedIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
                    tp.setHasReceivedBye(s.hasReceivedBye());
                    return tp;
                })
                .collect(Collectors.toList());

        Set<Integer> pairedPlayerIds = new HashSet<>();
        int tableNumber = 1;

        TournamentPlayer byePlayer = null;
        if (availablePlayers.size() % 2 != 0) {
            for (int i = availablePlayers.size() - 1; i >= 0; i--) {
                TournamentPlayer potentialByePlayer = availablePlayers.get(i);
                if (!potentialByePlayer.hasReceivedBye()) {
                    byePlayer = potentialByePlayer;
                    break;
                }
            }
            if (byePlayer == null && !availablePlayers.isEmpty()) {
                byePlayer = availablePlayers.get(availablePlayers.size() - 1);
            }

            if (byePlayer != null) {
                Match byeMatch = new Match(0, byePlayer.getPlayerId(), null, tableNumber++);
                byeMatch.setPlayer1Details(byePlayer.getPlayerDetails());
                pairings.add(byeMatch);
                pairedPlayerIds.add(byePlayer.getPlayerId());
                // Note: Actual update to DB for 'hasReceivedBye' should happen upon saving the round.
                // Here we just mark for the current pairing generation.
                // The DAO call tournamentPlayerDAO.updatePlayerByeStatus will be done on save.
            }
        }

        if (forRoundNumber == 1) {
            // currentStandings are already sorted by initial Elo DESC by getCurrentStandings(..., 0)
            List<TournamentPlayer> round1Players = new ArrayList<>(availablePlayers);
            if(byePlayer != null) round1Players.remove(byePlayer); // Remove bye player from pairing pool

            for (int i = 0; i < round1Players.size(); i += 2) {
                TournamentPlayer player1 = round1Players.get(i);
                // Already handled by byePlayer logic if player1 was the one
                // if (pairedPlayerIds.contains(player1.getPlayerId())) continue;

                if (i + 1 < round1Players.size()) {
                    TournamentPlayer player2 = round1Players.get(i + 1);
                    Match match = new Match(0, player1.getPlayerId(), player2.getPlayerId(), tableNumber++);
                    match.setPlayer1Details(player1.getPlayerDetails());
                    match.setPlayer2Details(player2.getPlayerDetails());
                    pairings.add(match);
                    pairedPlayerIds.add(player1.getPlayerId());
                    pairedPlayerIds.add(player2.getPlayerId());
                } else {
                    // This case (single player left after processing pairs) should be handled by the bye logic.
                    // If it still occurs, it implies an issue in bye assignment or odd numbers not handled.
                    System.err.println("Lỗi logic xếp cặp vòng 1: Còn lại 1 người không có cặp sau khi đã thử xử lý bye: " + player1.getPlayerName());
                }
            }
        } else { // From Round 2 onwards
            Set<String> existingPairsInTournament = matchDAO.getPlayedPairsInTournament(tournamentId);
            List<TournamentPlayer> toPairPlayers = new ArrayList<>(availablePlayers);
            if(byePlayer != null) toPairPlayers.remove(byePlayer); // Remove bye player from pairing pool

            // Create a mutable list of players to pair for this round
            List<TournamentPlayer> currentRoundPairablePlayers = new ArrayList<>(toPairPlayers);


            while (pairedPlayerIds.size() < toPairPlayers.size()) {
                TournamentPlayer player1 = null;
                // Find the highest-ranked un-paired player
                for (TournamentPlayer p : currentRoundPairablePlayers) {
                    if (!pairedPlayerIds.contains(p.getPlayerId())) {
                        player1 = p;
                        break;
                    }
                }

                if (player1 == null) break; // All remaining players are somehow paired or no one left

                TournamentPlayer player2Found = null;
                for (TournamentPlayer p2 : currentRoundPairablePlayers) {
                    if (player1.getPlayerId() == p2.getPlayerId() || pairedPlayerIds.contains(p2.getPlayerId())) {
                        continue;
                    }
                    String pairKey = Math.min(player1.getPlayerId(), p2.getPlayerId()) + "-" + Math.max(player1.getPlayerId(), p2.getPlayerId());
                    if (!existingPairsInTournament.contains(pairKey)) {
                        player2Found = p2;
                        break;
                    }
                }

                if (player2Found != null) {
                    Match match = new Match(0, player1.getPlayerId(), player2Found.getPlayerId(), tableNumber++);
                    match.setPlayer1Details(player1.getPlayerDetails());
                    match.setPlayer2Details(player2Found.getPlayerDetails());
                    pairings.add(match);
                    pairedPlayerIds.add(player1.getPlayerId());
                    pairedPlayerIds.add(player2Found.getPlayerId());
                } else {
                    // Cannot find an opponent for player1 under current rules (e.g., everyone else already played)
                    // This might require a more complex pairing adjustment (e.g. allowing rematches with penalty, or different bye assignment)
                    // For now, we log and this player might remain unpaired if not handled by bye.
                    System.err.println("Không tìm thấy đối thủ hợp lệ cho: " + player1.getPlayerName() + " (ID: " + player1.getPlayerId() + ") cho Vòng " + forRoundNumber);
                    // If this player was not the byePlayer, and we can't find a pair, this is problematic.
                    // The initial bye logic tries to assign one bye. If more byes are needed, or this happens,
                    // the tournament might need manual intervention or different pairing rules.
                    // For robust Swiss, sometimes a player might have to play someone they've played before if no other option.
                    // Our current rule "chưa gặp cờ thủ đang xem xét" is strict.
                    pairedPlayerIds.add(player1.getPlayerId()); // Mark as processed to avoid infinite loop. This player is effectively "unpaired" by the algorithm.
                }
            }
        }
        return pairings;
    }

    public boolean saveNewRoundAndPairings(int tournamentId, int roundNumber, List<Match> pairings) {
        // Kiểm tra đầu vào
        if (tournamentId <= 0 || roundNumber <= 0 || pairings == null || pairings.isEmpty()) {
            System.err.println("Dữ liệu đầu vào không hợp lệ");
            return false;
        }

        // Kiểm tra xem vòng đấu đã tồn tại chưa
        List<Round> existingRounds = roundDAO.getRoundsByTournament(tournamentId);
        for (Round round : existingRounds) {
            if (round.getRoundNumber() == roundNumber) {
                System.err.println("Vòng đấu " + roundNumber + " đã tồn tại trong giải đấu này");
                return false;
            }
        }

        // Tạo Round mới
        Round newRound = new Round();
        newRound.setTournamentId(tournamentId);
        newRound.setRoundNumber(roundNumber);
        newRound.setName("Vòng " + roundNumber);
        newRound.setStartTime(new Date());
        newRound.setStatus("PENDING");

        Round savedRound = roundDAO.saveRound(newRound);
        if (savedRound == null || savedRound.getId() == 0) {
            System.err.println("Không thể lưu vòng đấu mới.");
            return false;
        }

        // Gán roundId cho các match và xử lý BYE
        for (Match match : pairings) {
            match.setRoundId(savedRound.getId());
            if (match.getPlayer2Id() == null) { // BYE match
                match.setResult("BYE");
                // Cập nhật trạng thái BYE cho người chơi
                if (!tournamentPlayerDAO.updatePlayerByeStatus(tournamentId, match.getPlayer1Id())) {
                    System.err.println("Không thể cập nhật trạng thái BYE cho người chơi " + match.getPlayer1Id());
                    return false;
                }
            }
        }

        // Lưu các trận đấu
        boolean matchesSaved = matchDAO.saveMatches(pairings, savedRound.getId());
        if (!matchesSaved) {
            System.err.println("Không thể lưu các trận đấu.");
            return false;
        }

        return true;
    }
}