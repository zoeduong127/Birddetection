-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: baddb
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `tel` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (14,'testaccount','testaccount@gmail.com','fa0a00b683b3434adf5baef1d1ed2854b068ba04a73f61d8d50803efc37723d6','','��&ȡ�\"U\"(��}��{'),(69,'lucaf','lucafuertes04@gmail.com','817b5c0013c608c932ee2c4d650dab3212ebce1ae14e60b5b8360ff294520c68','0681254054','mz͙$�,�\0�������,�'),(72,'raspi','raspi@gmail.com','9cadd911be48eaf232898a76d8eb82accc0d784dc5e4e91f71349fd208ddecf9','0681254054','f����k\\x�1%�U���Z');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archived_image`
--

DROP TABLE IF EXISTS `archived_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archived_image` (
  `image_id` int NOT NULL AUTO_INCREMENT,
  `visit_id` int NOT NULL,
  `date` datetime DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `visit_id_idx` (`visit_id`),
  CONSTRAINT `visit_id_archive` FOREIGN KEY (`visit_id`) REFERENCES `visit` (`visit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archived_image`
--

LOCK TABLES `archived_image` WRITE;
/*!40000 ALTER TABLE `archived_image` DISABLE KEYS */;
INSERT INTO `archived_image` VALUES (2,1,'2008-11-09 00:00:00','meta/img/main/bird1.jpeg'),(5,51,'2008-11-09 00:00:00','meta/img/main/bird1.jpeg');
/*!40000 ALTER TABLE `archived_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bird_image`
--

DROP TABLE IF EXISTS `bird_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bird_image` (
  `image_id` int NOT NULL AUTO_INCREMENT,
  `visit_id` int DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `visit_id_idx` (`visit_id`),
  CONSTRAINT `visit_id` FOREIGN KEY (`visit_id`) REFERENCES `visit` (`visit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bird_image`
--

LOCK TABLES `bird_image` WRITE;
/*!40000 ALTER TABLE `bird_image` DISABLE KEYS */;
INSERT INTO `bird_image` VALUES (1,1,'2008-11-09 15:45:21','meta/img/main/bird1.jpeg'),(2,1,'2008-11-11 13:23:44','meta/img/main/bird2.jpeg'),(3,1,'2008-11-11 13:23:44','meta/img/main/bird3.jpeg'),(4,2,'2023-10-09 12:46:12','meta/img/main/bird4.jpeg'),(5,2,'2023-10-09 12:47:21','meta/img/main/bird5.jpeg'),(6,2,'2023-10-09 12:45:32','meta/img/main/bird6.jpeg'),(7,3,'2023-10-15 08:59:34','meta/img/main/bird7.jpeg'),(8,3,'2023-10-17 08:48:21','meta/img/main/bird8.jpeg'),(9,3,'2023-10-13 08:46:21','meta/img/main/bird9.jpeg'),(11,3,'2023-10-13 00:00:00','meta/img/main/bird10.jpeg'),(38,52,'2008-11-09 00:00:00','meta/img/main/bird1.jpeg'),(39,53,'2008-11-09 00:00:00','meta/img/main/bird1.jpeg'),(40,54,'1970-01-20 00:00:00','meta/img/main/bird1.jpeg'),(41,55,'1970-01-20 00:00:00','meta/img/main/bird1.jpeg');
/*!40000 ALTER TABLE `bird_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bird_image_temp`
--

DROP TABLE IF EXISTS `bird_image_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bird_image_temp` (
  `image_id` int NOT NULL AUTO_INCREMENT,
  `visit_id` int DEFAULT NULL,
  `data` datetime DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `bird_image_temp_ibfk_1` (`visit_id`),
  CONSTRAINT `bird_image_temp_ibfk_1` FOREIGN KEY (`visit_id`) REFERENCES `visit_temp` (`visit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bird_image_temp`
--

LOCK TABLES `bird_image_temp` WRITE;
/*!40000 ALTER TABLE `bird_image_temp` DISABLE KEYS */;
/*!40000 ALTER TABLE `bird_image_temp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reset_token`
--

DROP TABLE IF EXISTS `reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reset_token` (
  `token_id` int NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL,
  `token` varchar(255) NOT NULL,
  `expiration` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  KEY `account_id_idx` (`account_id`),
  CONSTRAINT `account_id_password_reset` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reset_token`
--

LOCK TABLES `reset_token` WRITE;
/*!40000 ALTER TABLE `reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token` (
  `token_id` int NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL,
  `token` varchar(255) NOT NULL,
  `expiration` timestamp NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  KEY `token_ibfk_1` (`account_id`),
  CONSTRAINT `token_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=368 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (366,14,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0YWNjb3VudCIsImFjY291bnRfaWQiOjE0LCJleHAiOjE2OTk0Mzg2MDV9.MypyCK8LQj7k-yHwxKaUjOOyqKSQanafCc7FG24U33s','2023-11-08 10:16:45','2023-11-07 10:16:45','2023-11-07 10:16:45'),(367,72,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYXNwaSIsImFjY291bnRfaWQiOjcyLCJleHAiOjE2OTk0NTYyNDJ9.YVjfzVs4Y9QyVdqUXTw-D25CpIlCtYNemHs7ALz-xP8','2023-11-08 15:10:42','2023-11-07 15:10:42','2023-11-07 15:10:42');
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visit`
--

DROP TABLE IF EXISTS `visit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visit` (
  `visit_id` int NOT NULL AUTO_INCREMENT,
  `species` varchar(255) DEFAULT NULL,
  `arrival` datetime DEFAULT NULL,
  `departure` datetime DEFAULT NULL,
  `visit_len` int DEFAULT NULL,
  `accuracy` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`visit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visit`
--

LOCK TABLES `visit` WRITE;
/*!40000 ALTER TABLE `visit` DISABLE KEYS */;
INSERT INTO `visit` VALUES (1,'Pigeon','2008-11-09 15:45:21','2008-11-11 13:23:44',310,95.12),(2,'Sparrow','2023-10-09 12:45:21','2023-10-09 12:50:21',124,89.23),(3,'Sparrow','2023-10-13 08:45:21','2023-10-13 08:56:21',24,89.12),(51,NULL,'2008-11-09 00:00:00','2008-11-11 00:00:00',310,95.12),(52,'Finch','2008-11-09 00:00:00','2008-11-11 00:00:00',310,95.12),(53,'Sparrow','2008-11-09 00:00:00','2008-11-11 00:00:00',310,95.12),(54,'Sparrow','1970-01-20 00:00:00','1970-01-20 00:00:00',310,95.12),(55,'Sparrow','1970-01-20 00:00:00','1970-01-20 00:00:00',310,99.75);
/*!40000 ALTER TABLE `visit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visit_temp`
--

DROP TABLE IF EXISTS `visit_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visit_temp` (
  `visit_id` int NOT NULL AUTO_INCREMENT,
  `arrival` datetime DEFAULT NULL,
  `departure` datetime DEFAULT NULL,
  `visit_len` int DEFAULT NULL,
  PRIMARY KEY (`visit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visit_temp`
--

LOCK TABLES `visit_temp` WRITE;
/*!40000 ALTER TABLE `visit_temp` DISABLE KEYS */;
/*!40000 ALTER TABLE `visit_temp` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-11-08  2:28:09
