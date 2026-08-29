-- Run in correct dependency order to avoid FK constraint failures
SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE IF NOT EXISTS parent_user (
    parent_id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    user_name VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(255),
    password VARCHAR(255),
    phone_number BIGINT,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS subject (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    icon_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS module (
    id CHAR(36) NOT NULL PRIMARY KEY,
    subject_id CHAR(36) NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    order_index INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (subject_id) REFERENCES subject(id)
);

CREATE TABLE IF NOT EXISTS sub_module (
    id CHAR(36) NOT NULL PRIMARY KEY,
    module_id CHAR(36) NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    order_index INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (module_id) REFERENCES module(id)
);

CREATE TABLE IF NOT EXISTS interactive_activity (
    id CHAR(36) NOT NULL PRIMARY KEY,
    sub_module_id CHAR(36) NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    order_index INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (sub_module_id) REFERENCES sub_module(id)
);

CREATE TABLE IF NOT EXISTS interactive_process (
    id CHAR(36) NOT NULL PRIMARY KEY,
    interactive_activity_id CHAR(36),
    sub_module_id CHAR(36),
    name TEXT,
    description TEXT,
    order_index INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (interactive_activity_id) REFERENCES interactive_activity(id),
    FOREIGN KEY (sub_module_id) REFERENCES sub_module(id)
);

CREATE TABLE IF NOT EXISTS interactive_process_substep (
    id CHAR(36) NOT NULL PRIMARY KEY,
    process_id CHAR(36),
    title TEXT,
    content TEXT,
    order_index INT,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (process_id) REFERENCES interactive_process(id)
);

CREATE TABLE IF NOT EXISTS digital_activity (
    id CHAR(36) NOT NULL PRIMARY KEY,
    sub_module_id CHAR(36) NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    order_index INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (sub_module_id) REFERENCES sub_module(id)
);

CREATE TABLE IF NOT EXISTS question (
    id CHAR(36) NOT NULL PRIMARY KEY,
    digital_activity_id CHAR(36),
    text TEXT,
    question_type VARCHAR(50),
    order_index INT,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (digital_activity_id) REFERENCES digital_activity(id)
);

CREATE TABLE IF NOT EXISTS question_option (
    id CHAR(36) NOT NULL PRIMARY KEY,
    question_id CHAR(36),
    option_text TEXT,
    is_correct BOOLEAN DEFAULT FALSE,
    order_index INT,
    FOREIGN KEY (question_id) REFERENCES question(id)
);

CREATE TABLE IF NOT EXISTS pricing_plan (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    duration_in_months INT,
    grade VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS pricing_plan_subject (
    id CHAR(36) NOT NULL PRIMARY KEY,
    pricing_plan_id CHAR(36),
    subject_id CHAR(36),
    FOREIGN KEY (pricing_plan_id) REFERENCES pricing_plan(id),
    FOREIGN KEY (subject_id) REFERENCES subject(id)
);

CREATE TABLE IF NOT EXISTS wallet (
    id CHAR(36) NOT NULL PRIMARY KEY,
    parent_id CHAR(36) NOT NULL UNIQUE,
    balance INT,
    status VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS wallet_transaction (
    id CHAR(36) NOT NULL PRIMARY KEY,
    wallet_id CHAR(36),
    amount INT,
    type VARCHAR(50),
    description TEXT,
    created_at DATETIME,
    FOREIGN KEY (wallet_id) REFERENCES wallet(id)
);

CREATE TABLE IF NOT EXISTS school (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name TEXT,
    address TEXT,
    city VARCHAR(255),
    state VARCHAR(255),
    pincode VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS child_user (
    child_id CHAR(36) NOT NULL PRIMARY KEY,
    child_name VARCHAR(255),
    gender VARCHAR(50),
    grade VARCHAR(50),
    date_of_birth DATE,
    blood_group VARCHAR(10),
    age_group VARCHAR(50),
    phone_number BIGINT,
    parent_id CHAR(36),
    school_id VARCHAR(255),
    active_plan_id VARCHAR(255),
    plan_start_date DATE,
    plan_expiry_date DATE,
    plan_status VARCHAR(50),
    FOREIGN KEY (parent_id) REFERENCES parent_user(parent_id)
);

CREATE TABLE IF NOT EXISTS child_submodule_completion (
    id CHAR(36) NOT NULL PRIMARY KEY,
    child_id CHAR(36),
    sub_module_id CHAR(36),
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at DATETIME,
    FOREIGN KEY (child_id) REFERENCES child_user(child_id),
    FOREIGN KEY (sub_module_id) REFERENCES sub_module(id)
);

CREATE TABLE IF NOT EXISTS child_interactive_activity_progress (
    id CHAR(36) NOT NULL PRIMARY KEY,
    child_id CHAR(36),
    interactive_activity_id CHAR(36),
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at DATETIME,
    FOREIGN KEY (child_id) REFERENCES child_user(child_id),
    FOREIGN KEY (interactive_activity_id) REFERENCES interactive_activity(id)
);

CREATE TABLE IF NOT EXISTS child_digital_activity_progress (
    id CHAR(36) NOT NULL PRIMARY KEY,
    child_id CHAR(36),
    digital_activity_id CHAR(36),
    is_completed BOOLEAN DEFAULT FALSE,
    score INT,
    completed_at DATETIME,
    FOREIGN KEY (child_id) REFERENCES child_user(child_id),
    FOREIGN KEY (digital_activity_id) REFERENCES digital_activity(id)
);

CREATE TABLE IF NOT EXISTS child_question_attempt (
    id CHAR(36) NOT NULL PRIMARY KEY,
    child_id CHAR(36),
    question_id CHAR(36),
    option_id CHAR(36),
    is_correct BOOLEAN,
    attempted_at DATETIME,
    FOREIGN KEY (child_id) REFERENCES child_user(child_id),
    FOREIGN KEY (question_id) REFERENCES question(id)
);

CREATE TABLE IF NOT EXISTS interactive_process_tracking (
    id CHAR(36) NOT NULL PRIMARY KEY,
    child_id CHAR(36),
    interactive_process_id CHAR(36),
    status VARCHAR(50),
    started_at DATETIME,
    completed_at DATETIME,
    FOREIGN KEY (child_id) REFERENCES child_user(child_id),
    FOREIGN KEY (interactive_process_id) REFERENCES interactive_process(id)
);

CREATE TABLE IF NOT EXISTS referral_code (
    id CHAR(36) NOT NULL PRIMARY KEY,
    parent_id CHAR(36) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    created_at DATETIME,
    FOREIGN KEY (parent_id) REFERENCES parent_user(parent_id)
);

CREATE TABLE IF NOT EXISTS referral_usage (
    id CHAR(36) NOT NULL PRIMARY KEY,
    referral_code_id CHAR(36),
    used_by_parent_id CHAR(36),
    used_at DATETIME,
    FOREIGN KEY (referral_code_id) REFERENCES referral_code(id),
    FOREIGN KEY (used_by_parent_id) REFERENCES parent_user(parent_id)
);

CREATE TABLE IF NOT EXISTS payment_transaction (
    id CHAR(36) NOT NULL PRIMARY KEY,
    parent_id CHAR(36),
    amount INT,
    currency VARCHAR(10),
    status VARCHAR(50),
    razorpay_order_id VARCHAR(255),
    razorpay_payment_id VARCHAR(255),
    created_at DATETIME,
    FOREIGN KEY (parent_id) REFERENCES parent_user(parent_id)
);

CREATE TABLE IF NOT EXISTS master_transaction (
    id CHAR(36) NOT NULL PRIMARY KEY,
    parent_id CHAR(36),
    amount INT,
    type VARCHAR(50),
    description TEXT,
    created_at DATETIME,
    FOREIGN KEY (parent_id) REFERENCES parent_user(parent_id)
);

CREATE TABLE IF NOT EXISTS plan_purchase (
    id CHAR(36) NOT NULL PRIMARY KEY,
    parent_id CHAR(36),
    child_id CHAR(36),
    pricing_plan_id CHAR(36),
    purchased_at DATETIME,
    expires_at DATETIME,
    status VARCHAR(50),
    FOREIGN KEY (parent_id) REFERENCES parent_user(parent_id),
    FOREIGN KEY (child_id) REFERENCES child_user(child_id),
    FOREIGN KEY (pricing_plan_id) REFERENCES pricing_plan(id)
);

CREATE TABLE IF NOT EXISTS campaign (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    start_date DATETIME,
    end_date DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME
);

SET FOREIGN_KEY_CHECKS=1;
