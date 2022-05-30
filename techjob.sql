CREATE DATABASE IF NOT EXISTS `techjob` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `techjob`;
-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: techjob
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `applies`
--

DROP TABLE IF EXISTS `applies`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `applies`
(
    `user_id`     INT  NOT NULL,
    `job_id`      INT  NOT NULL,
    `create_date` DATE NOT NULL,
    PRIMARY KEY (`user_id`, `job_id`),
    KEY `FK_Apply_JobId` (`job_id`),
    KEY `idx_apply` (`user_id`, `job_id`),
    CONSTRAINT `FK_Apply_JobId` FOREIGN KEY (`job_id`)
        REFERENCES `jobs` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `FK_Apply_UserId` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8MB4
  COLLATE = UTF8MB4_0900_AI_CI;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `companies`
--

DROP TABLE IF EXISTS `companies`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `companies`
(
    `id`          INT                                                     NOT NULL AUTO_INCREMENT,
    `avatar`      TEXT                                                    NOT NULL,
    `name`        VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `email`       VARCHAR(320) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `password`    VARBINARY(500)                                          NOT NULL,
    `phone`       VARCHAR(20) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI  NOT NULL,
    `city`        VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `address`     TEXT                                                    NOT NULL,
    `introduce`   TEXT,
    `verify_code` VARCHAR(100)                                            NOT NULL,
    `state`       VARCHAR(20)                                             NOT NULL,
    `create_by`   VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `create_date` DATE                                                    NOT NULL,
    `update_by`   VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `update_date` DATE                                                    NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company` (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = UTF8MB4
  COLLATE = UTF8MB4_0900_AI_CI;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `files`
--

DROP TABLE IF EXISTS `files`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `files`
(
    `id`          INT                                                     NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `url`         TEXT                                                    NOT NULL,
    `user_id`     INT                                                     NOT NULL,
    `create_date` DATE                                                    NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_File_UserId` (`user_id`),
    KEY `idx_file` (`id`),
    CONSTRAINT `FK_File_UserId` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE = INNODB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = UTF8MB4
  COLLATE = UTF8MB4_0900_AI_CI;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `followings`
--

DROP TABLE IF EXISTS `followings`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `followings`
(
    `id`          INT  NOT NULL AUTO_INCREMENT,
    `user_id`     INT  NOT NULL,
    `company_id`  INT DEFAULT NULL,
    `job_id`      INT DEFAULT NULL,
    `create_date` DATE NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_Following_UserId` (`user_id`),
    KEY `FK_Following_CompanyId` (`company_id`),
    KEY `FK_Following_JobId` (`job_id`),
    KEY `idx_following` (`id`),
    CONSTRAINT `FK_Following_CompanyId` FOREIGN KEY (`company_id`)
        REFERENCES `companies` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `FK_Following_JobId` FOREIGN KEY (`job_id`)
        REFERENCES `jobs` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `FK_Following_UserId` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8MB4
  COLLATE = UTF8MB4_0900_AI_CI;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jobs`
--

DROP TABLE IF EXISTS `jobs`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jobs`
(
    `id`             INT                                                     NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `number_recruit` INT                                                     NOT NULL,
    `gender`         VARCHAR(10) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI DEFAULT NULL,
    `salary`         VARCHAR(100) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `academic level` VARCHAR(100) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `experience`     TEXT                                                    NOT NULL,
    `working_form`   VARCHAR(100) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `welfare`        TEXT                                                    NOT NULL,
    `description`    TEXT                                                    NOT NULL,
    `requirement`    TEXT                                                    NOT NULL,
    `deadline`       DATE                                                    NOT NULL,
    `company_id`     INT                                                    DEFAULT NULL,
    `type_id`        INT                                                    DEFAULT NULL,
    `state`          VARCHAR(20)                                             NOT NULL,
    `create_by`      VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `create_date`    DATE                                                    NOT NULL,
    `update_by`      VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `update_date`    DATE                                                    NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_Job_CompanyId` (`company_id`),
    KEY `FK_Job_TypeId` (`type_id`),
    KEY `idx_job` (`id`),
    CONSTRAINT `FK_Job_CompanyId` FOREIGN KEY (`company_id`)
        REFERENCES `companies` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `FK_Job_TypeId` FOREIGN KEY (`type_id`)
        REFERENCES `types` (`id`)
        ON DELETE CASCADE
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8MB4
  COLLATE = UTF8MB4_0900_AI_CI;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `types`
--

DROP TABLE IF EXISTS `types`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `types`
(
    `id`   INT                                                     NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_type` (`id`)
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8MB4
  COLLATE = UTF8MB4_0900_AI_CI;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users`
(
    `id`          INT                                                     NOT NULL AUTO_INCREMENT,
    `avatar`      TEXT                                                    NOT NULL,
    `name`        VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `sex`         VARCHAR(10) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI DEFAULT NULL,
    `dob`         DATE                                                   DEFAULT NULL,
    `email`       VARCHAR(320) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `password`    VARBINARY(500)                                          NOT NULL,
    `phone`       VARCHAR(20) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI  NOT NULL,
    `address`     TEXT,
    `verify_code` VARCHAR(100)                                            NOT NULL,
    `role`        VARCHAR(20) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI  NOT NULL,
    `state`       VARCHAR(20)                                             NOT NULL,
    `create_by`   VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `create_date` DATE                                                    NOT NULL,
    `update_by`   VARCHAR(255) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI NOT NULL,
    `update_date` DATE                                                    NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_User_RoleId` (`role`),
    KEY `idx_user` (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = UTF8MB4
  COLLATE = UTF8MB4_0900_AI_CI;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2022-05-23 19:18:30
