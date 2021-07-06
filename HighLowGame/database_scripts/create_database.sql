CREATE DATABASE  IF NOT EXISTS `highlowgame` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_swedish_ci */;
USE `highlowgame`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
  `id` char(36) NOT NULL,
  `name` varchar(300) NOT NULL,
  `email` varchar(60) DEFAULT NULL,
  `avatar_content` longblob DEFAULT NULL,
  `avatar_content_type` varchar(255) DEFAULT NULL,
  `avatar_content_size` bigint(20) DEFAULT NULL,
  `admin` tinyint(1) DEFAULT 0,
  `disabled` tinyint(1) DEFAULT 0,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` char(36) NOT NULL,
  `game` char(36) NOT NULL,
  `owner` char(36) DEFAULT NULL,
  `number` int(11) NOT NULL,
  `question` varchar(2048),
  `correct_answer` decimal(25,4) NOT NULL,
  `unit` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_game_1` (`game`),
  KEY `fk_owner_2` (`owner`),
  CONSTRAINT `fk_game_1` FOREIGN KEY (`game`) REFERENCES `game` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_owner_2` FOREIGN KEY (`owner`) REFERENCES `game_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `game_participant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_participant` (
  `id` char(36) NOT NULL,
  `game` char(36) NOT NULL,
  `player` char(36) NOT NULL,
  `number` int(11) NOT NULL,
  `points` int(11),
  PRIMARY KEY (`id`),
  KEY `fk_game_2` (`game`),
  KEY `fk_player_2` (`player`),
  CONSTRAINT `fk_game_2` FOREIGN KEY (`game`) REFERENCES `game` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_player_2` FOREIGN KEY (`player`) REFERENCES `player` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `game_participant_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_participant_answer` (
  `id` char(36) NOT NULL,
  `game_participant` char(36) NOT NULL,
  `question` char(36) NOT NULL,
  `answer` decimal(25,4) DEFAULT NULL,
  `high_low_answer` varchar(255) NOT NULL,
  `correct` tinyint(1),
  `points_before` int(11),
  `points_bet` int(11),
  `points_after` int(11),
  PRIMARY KEY (`id`),
  KEY `fk_game_participant_3` (`game_participant`),
  KEY `fk_question_3` (`question`),
  CONSTRAINT `fk_game_participant_3` FOREIGN KEY (`game_participant`) REFERENCES `game_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_question_3` FOREIGN KEY (`question`) REFERENCES `question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `game_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_tracker` (
  `id` char(36) NOT NULL,
  `game` char(36) NOT NULL,
  `current_question` char(36) DEFAULT NULL,
  `participant_posting_current_question` char(36) DEFAULT NULL,
  `participant_to_answer_first` char(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_game_3` (`game`),
  KEY `fk_question_2` (`current_question`),
  KEY `fk_participant_3` (`participant_posting_current_question`),
  KEY `fk_participant_4` (`participant_to_answer_first`),
  CONSTRAINT `fk_game_3` FOREIGN KEY (`game`) REFERENCES `game` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_question_2` FOREIGN KEY (`current_question`) REFERENCES `question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_participant_3` FOREIGN KEY (`participant_posting_current_question`) REFERENCES `game_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_participant_4` FOREIGN KEY (`participant_to_answer_first`) REFERENCES `game_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `game` (
  `id` char(36) NOT NULL,
  `game_tracker` char(36) DEFAULT NULL,
  `owner` char(36) NOT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `starting_points` int(11) NOT NULL,
  `game_type` varchar(255) NOT NULL,
  `game_leader` char(36),
  `questions_per_player` int(11),
  `created` datetime DEFAULT NULL,
  `started` datetime DEFAULT NULL,
  `finished` datetime DEFAULT NULL,
  `game_status` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_game_tracker_1` (`game_tracker`),
  KEY `fk_owner_1` (`owner`),
  KEY `fk_game_leader_1` (`game_leader`),
  CONSTRAINT `fk_game_tracker_1` FOREIGN KEY (`game_tracker`) REFERENCES `game_tracker` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_owner_1` FOREIGN KEY (`owner`) REFERENCES `player` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_leader_1` FOREIGN KEY (`game_leader`) REFERENCES `player` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
