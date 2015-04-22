DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS GamePool;
DROP TABLE IF EXISTS MatchSession;

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
	matchID				VARCHAR(10) PRIMARY KEY,
	userID				VARCHAR(10),
	opponentUserID                  VARCHAR(10),
	currentQuestion                 SMALLINT,
        opponentCurrentQuestion		SMALLINT,
	currentMatchScore               FLOAT,
        opponentCurrentMatchScore       FLOAT,	
	CONSTRAINT fk_MatchSession_userID FOREIGN KEY (userID) REFERENCES Users(userID),
	CONSTRAINT fk_MatchSession_opponentUserID FOREIGN KEY (opponentUserID) REFERENCES Users(userID)
);


