CREATE DATABASE IF NOT EXISTS BKChat;

USE BKChat;

-- user (id, userName, userPassword, userNickname, user_isAdmin)
-- message (id, timeSent, timeRecv, content, userFrom, userTo)

DROP TABLE IF EXISTS tbl_message;
DROP TABLE IF EXISTS tbl_user;

CREATE TABLE IF NOT EXISTS tbl_user (
    id INT NOT NULL AUTO_INCREMENT,
    userName VARCHAR(64) NOT NULL,
    userPassword NVARCHAR(256) NOT NULL,
    userNickname NVARCHAR(128) NOT NULL DEFAULT 'Default Nickname',
    user_isAdmin BOOLEAN NOT NULL DEFAULT FALSE,
    userAvatar NVARCHAR(256) NOT NULL DEFAULT 'avatar-default.jpg',
    
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tbl_message (
    id INT NOT NULL AUTO_INCREMENT,
    timeSent TIMESTAMP NOT NULL DEFAULT NOW(),
    timeRecv TIMESTAMP NOT NULL DEFAULT NOW(),
    content NVARCHAR(4096) DEFAULT '',
    userFrom INT NOT NULL,
    userTo INT NOT NULL,
    
    PRIMARY KEY (id),
    FOREIGN KEY (userFrom) REFERENCES BKChat.tbl_user (id),
    FOREIGN KEY (userTo) REFERENCES BKChat.tbl_user(id)
);

-- ADD SAMPLE ADMIN --
INSERT INTO tbl_user (userName, userPassword, userNickname, user_isAdmin, userAvatar) VALUES("admin", "bkcadmin", "Admin", TRUE, "avatar-default.jpg");
-- ADD SOME SAMPLE USERS --
INSERT INTO tbl_user (userName, userPassword, userNickname, user_isAdmin, userAvatar) VALUES("usertest", "123456", "Testing User", FALSE, "avatar1.jpg");
INSERT INTO tbl_user (userName, userPassword, userNickname, user_isAdmin, userAvatar) VALUES("UserTest2", "12345678", "Testing User 2", FALSE, "avatar2.jpg");

-- User Stored Procedure (USP) for Signing up... --
DELIMITER $$
DROP PROCEDURE IF EXISTS USP_Signup $$
CREATE PROCEDURE USP_Signup(IN user_Name NVARCHAR(64), IN user_Password NVARCHAR(64), IN user_Nickname NVARCHAR(128))
BEGIN 
	DECLARE counted INT;
    
	SET counted = (SELECT COUNT(*) FROM tbl_user WHERE userName = user_Name);
    
    IF counted = 0
    THEN INSERT INTO BKChat.tbl_user (userName, userPassword, userNickname, user_isAdmin) 
			VALUES(user_Name, user_Password, user_Nickname, FALSE);
    END IF;
END; $$
DELIMITER ;

INSERT INTO tbl_message (content, userFrom, userTo) VALUES('Some text', 1, 2);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('ABC', 2, 1);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('From Admin to User 2', 1, 3);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('From Testing User 2 to Admin', 3, 1);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('Nothing to do', 1, 2);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('DEF GHI', 2, 1);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('Admin Recv by Testing User 2', 3, 1);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('Testing User 2 Recv by Admin', 1, 3);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('Very long message Very long message Very long message Very long message Very long message Very long message Very long message Very long message Very long message ', 2, 1);
INSERT INTO tbl_message (content, userFrom, userTo) VALUES('Very long message Very long message Very long message Very long message Very long message Very long message Very long message ', 1, 2);


-- User Stored Procedure (USP) for Query all Message of 2 user... --
DELIMITER $$
DROP PROCEDURE IF EXISTS USP_GetMessageList $$
CREATE PROCEDURE USP_GetMessageList(IN user1_id INT, IN user2_id INT)
BEGIN 
	SELECT * FROM BKChat.tbl_message WHERE (
		(userFrom = user1_id AND userTo = user2_id) OR 
        (userFrom = user2_id AND userTo = user1_id)
	) ORDER BY timeSent ASC;
END; $$
DELIMITER ;

-- User Stored Procedure (USP) for Insert (Send) a Message... --
DELIMITER $$
DROP PROCEDURE IF EXISTS USP_InsertMessage $$
CREATE PROCEDURE USP_InsertMessage(IN msgContent NVARCHAR(4096), IN userFromId INT, IN userToId INT)
BEGIN 
	INSERT INTO BKChat.tbl_message (content, userFrom, userTo) VALUES (msgContent, userFromId, userToId);
    
    SELECT * FROM BKChat.tbl_message WHERE id = last_insert_id();
END; $$
DELIMITER ;

SELECT * FROM BKChat.tbl_user;
SELECT * FROM BKChat.tbl_message;

