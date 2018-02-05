-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 04, 2018 at 09:13 PM
-- Server version: 5.7.19-0ubuntu0.16.04.1
-- PHP Version: 7.0.22-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `survey`
--

--
-- Dumping data for table `tbl_form`
--

INSERT INTO `tbl_form` (`id`, `name`, `description`, `filepath`, `status`, `last_update`, `version`, `json`, `display`) VALUES
(1, 'Site', 'Manage Survey Sites', NULL, NULL, '2018-01-27 13:54:13', NULL, '{\r\n    "id": "1",\r\n    "name": "Site",\r\n    "description": "Manage Survey Sites",\r\n    "groups": [\r\n        {\r\n            "id": "group-1",\r\n            "name": "SiteDetails",\r\n            "label": "Site Details",\r\n            "inputs": [\r\n                {\r\n                    "id": "input-1",\r\n                    "name": "SiteDetails.Name",\r\n                    "label": "Name"\r\n                },\r\n                {\r\n                    "id": "input-2",\r\n                    "name": "SiteDetails.Country",\r\n                    "label": "Country"\r\n                },\r\n                {\r\n                    "id": "input-3",\r\n                    "name": "SiteDetails.Region",\r\n                    "label": "Region"\r\n                },\r\n                {\r\n                    "id": "input-4",\r\n                    "name": "SiteDetails.District",\r\n                    "label": "District"\r\n                },\r\n                {\r\n                    "id": "input-5",\r\n                    "name": "SiteDetails.Ward",\r\n                    "label": "Ward"\r\n                },\r\n                {\r\n                    "id": "input-6",\r\n                    "name": "SiteDetails.Village",\r\n                    "label": "Village"\r\n                }\r\n            ]\r\n        },\r\n        {\r\n            "id": "group-2",\r\n            "name": "SiteGPS",\r\n            "label": "Site GPS",\r\n            "type": "GPSLocationCapture",\r\n            "inputs": [\r\n                {\r\n                    "id": "input-7",\r\n                    "name": "SiteGPS.Latitude",\r\n                    "label": "Latitude"\r\n                },\r\n                {\r\n                    "id": "input-8",\r\n                    "name": "SiteGPS.Longitude",\r\n                    "label": "Longitude"\r\n                }\r\n            ]\r\n        }\r\n    ]\r\n}', 'SiteDetails.Name');

--
-- Dumping data for table `tbl_permission`
--

INSERT INTO `tbl_permission` (`id`, `name`, `description`, `parent`) VALUES
(1, 'ManageUsers', 'Create admin user2', NULL),
(2, 'ManagePermissions', 'Manage permissions', NULL),
(3, 'ManageRoles', 'Manage User Roles', NULL),
(4, 'Dummy', 'Dummy Desc', NULL),
(9, 'Dummy2', 'Dummmmmmy2', 4),
(11, 'deletePermission', 'Delete permission', 2),
(12, 'Survey', 'Survey', NULL);

--
-- Dumping data for table `tbl_role`
--

INSERT INTO `tbl_role` (`id`, `name`, `description`) VALUES
(1, 'Root', 'Root user role'),
(2, 'Admin', 'Administrator'),
(4, 'Dummy4', 'Dummy Desc'),
(5, 'Surveyor', 'Surveyor');

--
-- Dumping data for table `tbl_role_permission`
--

INSERT INTO `tbl_role_permission` (`id`, `permission_id`, `role_id`, `status`) VALUES
(1, 1, 1, 0),
(2, 1, 2, 0),
(3, 2, 1, 0),
(4, 3, 1, 0),
(5, 9, 2, 0),
(6, 11, 1, 0),
(7, 12, 5, 0);

--
-- Dumping data for table `tbl_user`
--

INSERT INTO `tbl_user` (`id`, `username`, `password`, `email`, `full_name`, `role_id`, `enabled`, `reset_on`) VALUES
(29, 'admin2', '$2a$10$0SNLmY9vm8/8nP03WX6NAOnv/A5ujIPNI/wZnmcpdJy52mU7F3pne', 'ezrankayamba@gmail.com', 'Ezra Nkayamba', 1, 1, 0),
(492, 'test1', '$2a$10$8DV8daoH7D2y4s11vye1SeebWeV3pes3grXz7Y8g9chI6TGLFkFsu', 'ezrankayamba@gmail.com', 'Ezra', 2, 1, 1),
(495, 'test2', '$2a$10$P3CRAdOox/FyY/BRwxZx7uCE/uC1mzMzy64GCsnqvwIPyIJQQ2OjC', 'ezrankayamba@gmail.com', 'Ezra', 1, 1, 1),
(551, 'test3', '$2a$10$rPWKvxSchyNa2FSeykuqd.3VPaENf55sVqwJ0Ro5kgVsEMWw8Ic/6', 'ezrankayamba@gmail.com', 'test3', 1, 1, 0),
(558, 'test4', '$2a$10$9XygzQv1ezSHWc9H4K/P4uYHC6CoyZiaIQHXsxbCGGXanhv3crIue', 'ezrankayamba@gmail.com', 'test4', 2, 1, 0),
(559, 'surveyor1', '$2a$10$ZjTWzFu1LnbfSJAjSlDJA.XZBmKFdU7LC/2KvLI0UHL7OqwDLo3Aq', 'ezrankayamba@gmail.com', 'Test Surveyor', 5, 1, 0),
(560, 'surveyor2', '$2a$10$g7e1Lz8IKTqfRa1zx4/VAucW8vhKX0a.AfQ0Cf2p7KvnLBGBR5EFa', 'ezrankayamba@gmail.com', 'Test Surveyor', 5, 1, 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
