/* indexes for USR table */
CREATE UNIQUE INDEX User_userId_index
ON USR USING BTREE (userId);

CREATE INDEX User_password_index
ON USR USING BTREE (password);

CREATE INDEX User_email_index
ON USR USING BTREE (email);

CREATE INDEX User_userName_index
ON USR USING BTREE (name);

CREATE INDEX User_dob_index
ON USR USING BTREE (dateOfBirth);

/* indexes for WORK_EXPR table */
CREATE INDEX Work_userId_index
ON WORK_EXPR USING BTREE (userId);

CREATE INDEX Work_company_index
ON WORK_EXPR USING BTREE (company);

CREATE INDEX Work_role_index
ON WORK_EXPR USING BTREE (role);

CREATE INDEX Work_location_index
ON WORK_EXPR USING BTREE (location);

CREATE INDEX Work_startDate_index
ON WORK_EXPR USING BTREE (startDate);

CREATE INDEX Work_endDate_index
ON WORK_EXPR USING BTREE (endDate);

/* indexes for EDUCATIONAL_DETAILS table */
CREATE INDEX Education_userId_index
ON EDUCATIONAL_DETAILS USING BTREE (userId);

CREATE INDEX Education_institute_index
ON EDUCATIONAL_DETAILS USING BTREE (instituitionName);

CREATE INDEX Education_major_index
ON EDUCATIONAL_DETAILS USING BTREE (major);

CREATE INDEX Education_degree_index
ON EDUCATIONAL_DETAILS USING BTREE (degree);

CREATE INDEX Education_startDate_index
ON EDUCATIONAL_DETAILS USING BTREE (startDate);

CREATE INDEX Education_endDate_index
ON EDUCATIONAL_DETAILS USING BTREE (endDate);

/* indexes for MESSAGE table */
CREATE UNIQUE INDEX Message_messageId_index
ON MESSAGE USING BTREE (msgId);

CREATE INDEX Message_senderId_index
ON MESSAGE USING BTREE (senderId);

CREATE INDEX Message_receiverId_index
ON MESSAGE USING BTREE (receiverId);

CREATE INDEX Message_contents_index
ON MESSAGE USING BTREE (contents);

CREATE INDEX Message_sendTime_index
ON MESSAGE USING BTREE (sendTime);

CREATE INDEX Message_delete_index
ON MESSAGE USING BTREE (deleteStatus);

CREATE INDEX Message_status_index
ON MESSAGE USING BTREE (status);

/* indexes for CONNECTION_USR table */
CREATE INDEX Connection_userId_index
ON CONNECTION_USR USING BTREE (userId);

CREATE INDEX Connection_connectionId_index
ON CONNECTION_USR USING BTREE (connectionId);

CREATE INDEX Connection_status_index
ON CONNECTION_USR USING BTREE (status);