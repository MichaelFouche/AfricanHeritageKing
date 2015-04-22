DROP TABLE IF EXISTS MatchSession;
DROP TABLE IF EXISTS GamePool;
DROP TABLE IF EXISTS Users;
CREATE TABLE Users
(
	userID                          VARCHAR(10) PRIMARY KEY,
	email                           VARCHAR(50),
	password			VARCHAR(15),
	score				FLOAT
);

CREATE TABLE GamePool
(
	userID				VARCHAR(10) PRIMARY KEY,	
	opponentUserID                  VARCHAR(10),
	CONSTRAINT fk_GamePool_userID FOREIGN KEY (userID) REFERENCES Users(userID),
	CONSTRAINT fk_GamePool_opponentUserID FOREIGN KEY (opponentUserID) REFERENCES Users(userID)
);

CREATE TABLE MatchSession
(
	matchSessionID			VARCHAR(10) PRIMARY KEY,
        matchID                         VARCHAR(10),
	userID				VARCHAR(10),
	opponentUserID                  VARCHAR(10),
	currentQuestion                 SMALLINT,        
	currentMatchScore               FLOAT,        
	CONSTRAINT fk_MatchSession_userID FOREIGN KEY (userID) REFERENCES Users(userID),
	CONSTRAINT fk_MatchSession_opponentUserID FOREIGN KEY (opponentUserID) REFERENCES Users(userID)
);


INSERT INTO users values('foosh','foosh@outlook.com','1@3',0);