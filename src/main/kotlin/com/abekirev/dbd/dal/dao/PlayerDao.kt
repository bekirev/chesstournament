package com.abekirev.dbd.dal.dao

import com.abekirev.dbd.dal.entity.OpponentDto
import com.abekirev.dbd.dal.entity.PlayerDto
import com.abekirev.dbd.dal.entity.PlayerGameDto
import com.abekirev.dbd.dal.repository.PlayerRepository
import com.abekirev.dbd.entity.Opponent
import com.abekirev.dbd.entity.Player
import com.abekirev.dbd.entity.PlayerGame
import com.abekirev.dbd.entity.PlayerGameResult.Draw
import com.abekirev.dbd.entity.PlayerGameResult.Lost
import com.abekirev.dbd.entity.PlayerGameResult.Won
import com.abekirev.dbd.entity.PlayerGameSide.Black
import com.abekirev.dbd.entity.PlayerGameSide.White
import com.abekirev.dbd.toList

class PlayerDao(private val playerRepository: PlayerRepository) {
    fun getAll(): Collection<Player> {
        return playerRepository.findAll().map(::playerDtoToPlayer)
    }

    fun getAllProjections(): Collection<Player> {
        return playerRepository.findAllProjections().map(::playerDtoToPlayer).toList()
    }

    fun get(id: String): Player? {
        return playerRepository.findById(id).map(::playerDtoToPlayer).orElse(null)
    }

    fun create(player: Player): String {
        return playerRepository.save(playerToPlayerDto(player)).id!!
    }

    fun update(player: Player) {
        playerRepository.save(playerToPlayerDto(player))
    }

    fun delete(id: String) {
        return playerRepository.delete(id)
    }
}

internal sealed class PlayerSide(val dbValue: String) {
    class White : PlayerSide("white")
    class Black : PlayerSide("black")
}

internal sealed class PlayerGameResult(val dbValue: String) {
    class Won : PlayerGameResult("won")
    class Lost : PlayerGameResult("lost")
    class Draw : PlayerGameResult("draw")
}

internal fun playerDtoToPlayer(player: PlayerDto): Player {
    return Player(
            player.id!!,
            player.firstName!!,
            player.lastName!!,
            player.games?.map(::playerGameDtoToPlayerGame) ?: throw IllegalArgumentException()
    )
}

internal fun playerGameDtoToPlayerGame(game: PlayerGameDto): PlayerGame {
    return PlayerGame(
            game.id!!,
            game.tournamentId!!,
            game.tournamentName!!,
            game.side?.let { side ->
                when (side) {
                    PlayerSide.White().dbValue -> White()
                    PlayerSide.Black().dbValue -> Black()
                    else -> throw IllegalArgumentException()
                }
            } ?: throw IllegalArgumentException(),
            game.opponent?.let(::opponentDtoToOpponent) ?: throw IllegalArgumentException(),
            game.result?.let { result ->
                when (result) {
                    com.abekirev.dbd.dal.dao.PlayerGameResult.Won().dbValue -> Won()
                    com.abekirev.dbd.dal.dao.PlayerGameResult.Draw().dbValue -> Lost()
                    com.abekirev.dbd.dal.dao.PlayerGameResult.Lost().dbValue -> Draw()
                    else -> throw IllegalArgumentException()
                }
            } ?: throw IllegalArgumentException()
    )
}

internal fun opponentDtoToOpponent(opponent: OpponentDto): Opponent {
    return Opponent(
            opponent.id!!,
            opponent.firstName!!,
            opponent.lastName!!
    )
}

internal fun playerToPlayerDto(player: Player): PlayerDto {
    return PlayerDto(
            player.id,
            player.firstName,
            player.lastName,
            player.games.map(::playerGameToPlayerGameDto)
    )
}

internal fun playerGameToPlayerGameDto(game: PlayerGame): PlayerGameDto {
    return PlayerGameDto(
            game.id,
            game.tournamentId,
            game.tournamentName,
            when (game.side) {
                is White -> PlayerSide.White().dbValue
                is Black -> PlayerSide.Black().dbValue
            },
            opponentToOpponentDto(game.opponent),
            when (game.result) {
                is Won -> PlayerGameResult.Won().dbValue
                is Lost -> PlayerGameResult.Lost().dbValue
                is Draw -> PlayerGameResult.Draw().dbValue
            }
    )
}

internal fun opponentToOpponentDto(opponent: Opponent): OpponentDto {
    return OpponentDto(
            opponent.id,
            opponent.firstName,
            opponent.lastName
    )
}
