package com.abekirev.dbd.web.controller

import com.abekirev.dbd.entity.IPlayer
import com.abekirev.dbd.service.PlayerService
import com.abekirev.dbd.service.TournamentService
import com.abekirev.dbd.web.LocalizedMessageSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.WebApplicationContext

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/tournament/")
class TournamentController @Autowired constructor(
        private val tournamentService: TournamentService,
        private val playerService: PlayerService,
        private val localizedMessageSource: LocalizedMessageSource
) {

    @GetMapping(params = arrayOf("id"))
    fun getById(modelMap: ModelMap, id: String): String {
        val tournament = tournamentService.getById(id)
        if (tournament != null) {
            modelMap.addAttribute("tournament", tournament)
        } else {
            modelMap.addAttribute("error", localizedMessageSource.getMessage("tournament.not_found"))
        }
        return "tournament/tournament"
    }

    @GetMapping("list/")
    fun list(modelMap: ModelMap): String {
        modelMap.addAttribute("tournaments", tournamentService.getAll())
        return "tournament/list"
    }

    @GetMapping("table/")
    fun table(modelMap: ModelMap): String {
//        val players = playerService.getAll()
//        val bergerCoefByPlayer = players
//                .map { player ->
//                    player.id to player.bergerCoef(players)
//                }.toMap()
//        val sortedPlayers = players
//                .sortedWith(Comparator { p1, p2 -> -bergerCoefByPlayer[p1.id]!!.compareTo(bergerCoefByPlayer[p2.id]!!) })
//                .map(Player::id)
//        val results = players.map { player ->
//            ResultRow(
//                    player,
//                    player.games.map { game ->
//                        when {
//                            player.isWhite(game) -> game.blackPlayer
//                            else -> game.whitePlayer
//                        }.id!! to when (player.gameResult(game)) {
//                            is PlayerGameResult.Won -> "1"
//                            is PlayerGameResult.Lost -> "0"
//                            is PlayerGameResult.Draw -> "1/2"
//                        }
//                    }.toMap(),
//                    player.points(),
//                    bergerCoefByPlayer[player.id]!!,
//                    1 + sortedPlayers.indexOf(player.id)
//            )
//        }
//        modelMap.addAttribute("results", results)
        return "tournament/table"
    }

    class ResultRow(private val p: IPlayer, val gameResults: Map<String, String>, val points: Double, val bergerCoef: Double, val place: Int?) : IPlayer by p

    @GetMapping("schedule/")
    fun schedule(modelMap: ModelMap): String {
        return "home"
    }
}