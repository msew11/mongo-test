package org.matrix.game

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.matrix.game.test.testMongo
import org.slf4j.LoggerFactory

fun main() {
    val logger: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    logger.level = Level.INFO

    testMongo()
}