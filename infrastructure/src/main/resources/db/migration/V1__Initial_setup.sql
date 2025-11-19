-- =============================================
-- COMPLETE DDL SCRIPT - DIET CONTROL DATABASE
-- Compatible with Docker PostgreSQL
-- Version 3.0 - Flat meal_record structure
-- Flyway Migration V1
-- =============================================
-- =============================================
-- CREATE TABLES (without constraints)
-- =============================================
-- Table 1: diet_plan
CREATE
    TABLE
        diet_plan(
            id SERIAL,
            name VARCHAR(100) NOT NULL,
            start_date DATE NOT NULL,
            end_date DATE,
            is_active BOOLEAN NOT NULL DEFAULT FALSE,
            daily_calories INTEGER,
            daily_protein_g INTEGER,
            daily_carbs_g INTEGER,
            daily_fat_g INTEGER,
            goal TEXT,
            nutritionist_notes TEXT,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        );

COMMENT ON
TABLE
    diet_plan IS 'Diet plans created by nutritionist - supports multiple plans over time';

COMMENT ON
COLUMN diet_plan.id IS 'Auto-incremented primary key';

COMMENT ON
COLUMN diet_plan.name IS 'Name/identifier of the diet plan (e.g., "Plano N°02 - Cutting")';

COMMENT ON
COLUMN diet_plan.start_date IS 'Date when this diet plan started';

COMMENT ON
COLUMN diet_plan.end_date IS 'Date when this diet plan ended (NULL if still valid)';

COMMENT ON
COLUMN diet_plan.is_active IS 'Indicates if this is the currently active plan - only ONE can be true';

COMMENT ON
COLUMN diet_plan.daily_calories IS 'Target daily calories (e.g., 2266)';

COMMENT ON
COLUMN diet_plan.daily_protein_g IS 'Target daily protein in grams (e.g., 186)';

COMMENT ON
COLUMN diet_plan.daily_carbs_g IS 'Target daily carbohydrates in grams (e.g., 288)';

COMMENT ON
COLUMN diet_plan.daily_fat_g IS 'Target daily fat in grams (e.g., 30)';

COMMENT ON
COLUMN diet_plan.goal IS 'Main goal of this plan (e.g., "Redução de gordura corporal com preservação da massa muscular")';

COMMENT ON
COLUMN diet_plan.nutritionist_notes IS 'Additional notes or observations from nutritionist';

COMMENT ON
COLUMN diet_plan.created_at IS 'System timestamp when the plan was created';

-- Table 2: meal_type
CREATE
    TABLE
        meal_type(
            id SERIAL,
            diet_plan_id INTEGER NOT NULL,
            name VARCHAR(50) NOT NULL,
            observation TEXT,
            scheduled_time TIME NOT NULL
        );

COMMENT ON
TABLE
    meal_type IS 'Registry of meal types for each diet plan';

COMMENT ON
COLUMN meal_type.id IS 'Auto-incremented primary key';

COMMENT ON
COLUMN meal_type.diet_plan_id IS 'Foreign key to diet_plan - links meal type to a specific plan';

COMMENT ON
COLUMN meal_type.name IS 'Meal type name: breakfast, lunch, snack, dinner, etc.';

COMMENT ON
COLUMN meal_type.observation IS 'Additional observations about the meal type, such as target macronutrients (protein, carbs, fat in grams) and total calories (kcal)';

COMMENT ON
COLUMN meal_type.scheduled_time IS 'Scheduled time for this meal (e.g., 06:20 for pre-workout)';

-- Table 3: standard_options
CREATE
    TABLE
        standard_options(
            id SERIAL,
            meal_type_id INTEGER NOT NULL,
            option_number INTEGER NOT NULL,
            description TEXT NOT NULL
        );

COMMENT ON
TABLE
    standard_options IS 'Standard meal plan options for each meal type';

COMMENT ON
COLUMN standard_options.id IS 'Auto-incremented primary key';

COMMENT ON
COLUMN standard_options.meal_type_id IS 'Foreign key to meal_type - indicates which meal this option belongs to';

COMMENT ON
COLUMN standard_options.option_number IS 'Option number (1, 2, 3, etc) within the meal type';

COMMENT ON
COLUMN standard_options.description IS 'Complete description of foods that compose this meal plan option';

-- Table 4: meal_record
CREATE
    TABLE
        meal_record(
            id SERIAL,
            consumed_at TIMESTAMP NOT NULL,
            meal_type_id INTEGER NOT NULL,
            is_free_meal BOOLEAN NOT NULL DEFAULT FALSE,
            standard_option_id INTEGER,
            free_meal_description TEXT,
            quantity DECIMAL(
                10,
                2
            ) NOT NULL DEFAULT 1.0,
            unit VARCHAR(50) NOT NULL DEFAULT 'serving',
            notes TEXT,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        );

COMMENT ON
TABLE
    meal_record IS 'Historical record of all food items consumed - one row per item';

COMMENT ON
COLUMN meal_record.id IS 'Auto-incremented primary key';

COMMENT ON
COLUMN meal_record.consumed_at IS 'Exact date and time when the item was consumed';

COMMENT ON
COLUMN meal_record.meal_type_id IS 'Foreign key indicating the meal type (breakfast, lunch, snack or dinner, ...)';

COMMENT ON
COLUMN meal_record.is_free_meal IS 'Boolean flag: TRUE = free meal (off-plan) | FALSE = standard meal (following plan)';

COMMENT ON
COLUMN meal_record.standard_option_id IS 'Foreign key to the chosen plan option (NULL when is_free_meal = TRUE)';

COMMENT ON
COLUMN meal_record.free_meal_description IS 'Text description of the free meal item (NULL when is_free_meal = FALSE)';

COMMENT ON
COLUMN meal_record.quantity IS 'Quantity of the item consumed (default: 1.0)';

COMMENT ON
COLUMN meal_record.unit IS 'Unit of measurement (serving, g, ml, units, etc)';

COMMENT ON
COLUMN meal_record.notes IS 'Optional field for additional observations';

COMMENT ON
COLUMN meal_record.created_at IS 'System timestamp when the record was inserted';

-- =============================================
-- ADD PRIMARY KEY CONSTRAINTS
-- =============================================
ALTER TABLE
    diet_plan ADD CONSTRAINT pk_diet_plan PRIMARY KEY(id);

COMMENT ON
CONSTRAINT pk_diet_plan ON
diet_plan IS 'Primary key constraint - ensures each diet plan has a unique identifier';

ALTER TABLE
    meal_type ADD CONSTRAINT pk_meal_type PRIMARY KEY(id);

COMMENT ON
CONSTRAINT pk_meal_type ON
meal_type IS 'Primary key constraint - ensures each meal type has a unique identifier';

ALTER TABLE
    standard_options ADD CONSTRAINT pk_standard_options PRIMARY KEY(id);

COMMENT ON
CONSTRAINT pk_standard_options ON
standard_options IS 'Primary key constraint - ensures each standard option has a unique identifier';

ALTER TABLE
    meal_record ADD CONSTRAINT pk_meal_record PRIMARY KEY(id);

COMMENT ON
CONSTRAINT pk_meal_record ON
meal_record IS 'Primary key constraint - ensures each meal record has a unique identifier';

-- =============================================
-- ADD FOREIGN KEY CONSTRAINTS
-- =============================================
ALTER TABLE
    meal_type ADD CONSTRAINT fk_meal_type_plan FOREIGN KEY(diet_plan_id) REFERENCES diet_plan(id) ON
    DELETE
        CASCADE;

COMMENT ON
CONSTRAINT fk_meal_type_plan ON
meal_type IS 'Foreign key to diet_plan - when a diet plan is deleted, all its meal types are also deleted (CASCADE)';

ALTER TABLE
    standard_options ADD CONSTRAINT fk_options_meal_type FOREIGN KEY(meal_type_id) REFERENCES meal_type(id) ON
    DELETE
        CASCADE;

COMMENT ON
CONSTRAINT fk_options_meal_type ON
standard_options IS 'Foreign key to meal_type - when a meal type is deleted, all its standard options are also deleted (CASCADE)';

ALTER TABLE
    meal_record ADD CONSTRAINT fk_record_meal_type FOREIGN KEY(meal_type_id) REFERENCES meal_type(id) ON
    DELETE
        RESTRICT;

COMMENT ON
CONSTRAINT fk_record_meal_type ON
meal_record IS 'Foreign key to meal_type - prevents deletion of meal types that have consumption records (RESTRICT)';

ALTER TABLE
    meal_record ADD CONSTRAINT fk_record_standard_option FOREIGN KEY(standard_option_id) REFERENCES standard_options(id) ON
    DELETE
    SET
        NULL;

COMMENT ON
CONSTRAINT fk_record_standard_option ON
meal_record IS 'Foreign key to standard_options - if a standard option is deleted, the reference becomes NULL but the meal record is preserved (SET NULL)';

-- =============================================
-- ADD UNIQUE CONSTRAINTS
-- =============================================
ALTER TABLE
    meal_type ADD CONSTRAINT uk_plan_meal_name UNIQUE(
        diet_plan_id,
        name
    );

COMMENT ON
CONSTRAINT uk_plan_meal_name ON
meal_type IS 'Unique constraint - ensures meal names (e.g., "Breakfast") are unique within each diet plan, but can repeat across different plans';

ALTER TABLE
    standard_options ADD CONSTRAINT uk_type_option UNIQUE(
        meal_type_id,
        option_number
    );

COMMENT ON
CONSTRAINT uk_type_option ON
standard_options IS 'Unique constraint - ensures option numbers (1, 2, 3, etc.) are unique within each meal type';

ALTER TABLE
    diet_plan ADD CONSTRAINT uk_one_active_plan EXCLUDE(
        is_active WITH =
    )
WHERE
    (
        is_active = TRUE
    );

COMMENT ON
CONSTRAINT uk_one_active_plan ON
diet_plan IS 'Exclusion constraint - ensures only ONE diet plan can be active (is_active = true) at any given time';

-- =============================================
-- ADD CHECK CONSTRAINTS
-- =============================================
ALTER TABLE
    meal_record ADD CONSTRAINT ck_free_meal_logic CHECK(
        (
            is_free_meal = TRUE
            AND standard_option_id IS NULL
            AND free_meal_description IS NOT NULL
        )
        OR(
            is_free_meal = FALSE
            AND standard_option_id IS NOT NULL
            AND free_meal_description IS NULL
        )
    );

COMMENT ON
CONSTRAINT ck_free_meal_logic ON
meal_record IS 'Check constraint - ensures data integrity: free meals must have description and no standard option, while planned meals must have standard option and no free description';

ALTER TABLE
    meal_record ADD CONSTRAINT ck_positive_quantity CHECK(
        quantity > 0
    );

COMMENT ON
CONSTRAINT ck_positive_quantity ON
meal_record IS 'Check constraint - ensures quantity is always greater than zero';

-- =============================================
-- CREATE INDEXES
-- =============================================
CREATE
    INDEX idx_plan_active ON
    diet_plan(is_active)
WHERE
    is_active = TRUE;

CREATE
    INDEX idx_plan_dates ON
    diet_plan(
        start_date,
        end_date
    );

CREATE
    INDEX idx_meal_type_plan ON
    meal_type(diet_plan_id);

CREATE
    INDEX idx_options_meal_type ON
    standard_options(meal_type_id);

CREATE
    INDEX idx_record_consumed_at ON
    meal_record(
        consumed_at DESC
    );

CREATE
    INDEX idx_record_meal_type ON
    meal_record(meal_type_id);

CREATE
    INDEX idx_record_free_meal ON
    meal_record(is_free_meal);

CREATE
    INDEX idx_record_date ON
    meal_record(
        DATE(consumed_at) DESC
    );

COMMENT ON
INDEX idx_plan_active IS 'Partial index to quickly find the active diet plan';

COMMENT ON
INDEX idx_plan_dates IS 'Composite index to optimize queries by plan date range';

COMMENT ON
INDEX idx_meal_type_plan IS 'Index to optimize finding meal types for a specific plan';

COMMENT ON
INDEX idx_options_meal_type IS 'Index to optimize searching options by meal type';

COMMENT ON
INDEX idx_record_consumed_at IS 'Index to optimize queries by consumption timestamp in descending order';

COMMENT ON
INDEX idx_record_meal_type IS 'Index to optimize joins and filters by meal type';

COMMENT ON
INDEX idx_record_free_meal IS 'Index to optimize filtering free meals vs standard meals';

COMMENT ON
INDEX idx_record_date IS 'Index to optimize queries by date';
