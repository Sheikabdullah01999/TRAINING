CREATE TABLE `employee` (
`emp_id` varchar(255) NOT NULL,
`assign_role` varchar(255) DEFAULT NULL,
`email` varchar(255) DEFAULT NULL,
`emp_department` varchar(255) DEFAULT NULL,
`emp_name` varchar(255) DEFAULT NULL,
`emp_password` varchar(255) DEFAULT NULL,
PRIMARY KEY (`emp_id`),
UNIQUE KEY `UKh09stjetuslh3jnq8xntscjkt` (`email`,`emp_id`)
)

CREATE TABLE `role` (
`id` bigint NOT NULL,
`role_description` varchar(255) DEFAULT NULL,
`role_name` varchar(255) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `UKgak8mxv46hsvetnf6xtm63qfi` (`id`,`role_name`)
)

CREATE TABLE `user_role` (
`emp_id` varchar(255) NOT NULL,
`role_id` bigint NOT NULL,
KEY `FKa68196081fvovjhkek5m97n3y` (`role_id`),
KEY `FKt4yi6vqxmq9tkvfwqje17ku5l` (`emp_id`),
CONSTRAINT `FKa68196081fvovjhkek5m97n3y` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
CONSTRAINT `FKt4yi6vqxmq9tkvfwqje17ku5l` FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
)

CREATE TABLE `device` (
`device_id` int NOT NULL,
`assign_status` varchar(255) DEFAULT NULL,
`category` varchar(255) DEFAULT NULL,
`device_name` varchar(255) DEFAULT NULL,
`device_purchase_date` date DEFAULT NULL,
`device_status` varchar(255) DEFAULT NULL,
`manufactured_id` varchar(255) DEFAULT NULL,
PRIMARY KEY (`device_id`),
UNIQUE KEY `UKeilm2gsjy35vlmfsxhagbu0js` (`manufactured_id`)
)

CREATE TABLE `employee_devices` (
`employee_emp_id` varchar(255) NOT NULL,
`devices_device_id` int NOT NULL,
UNIQUE KEY `UK_3g01l2qal31vls20v5ap2f0p0` (`devices_device_id`),
KEY `FKcydfiuwhk2mw7kpdch2lliibm` (`employee_emp_id`),
CONSTRAINT `FK25x151nx53pbeput8vbhtpb27` FOREIGN KEY (`devices_device_id`) REFERENCES `device` (`device_id`),
CONSTRAINT `FKcydfiuwhk2mw7kpdch2lliibm` FOREIGN KEY (`employee_emp_id`) REFERENCES `employee` (`emp_id`)
)

CREATE TABLE `employee_department` (
`id` bigint NOT NULL AUTO_INCREMENT,
`department` varchar(255) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `UKfhgo7f8tmp34yrmoqoufy05xm` (`id`,`department`)
)

CREATE TABLE `mapped_department` (
`department_id` bigint DEFAULT NULL,
`emp_id` varchar(255) NOT NULL,
PRIMARY KEY (`emp_id`),
KEY `FK35ddj387sw559emueo20o4ca8` (`department_id`),
CONSTRAINT `FK35ddj387sw559emueo20o4ca8` FOREIGN KEY (`department_id`) REFERENCES `employee_department` (`id`),
CONSTRAINT `FKrbx9fvxuhlqpjmpv6rn5ck666` FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
)

