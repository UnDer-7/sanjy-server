-- =============================================
-- DATABASE CLEANUP SCRIPT
-- Purpose: Clean all data and reset sequences
-- =============================================

-- Clean existing data (in reverse order of dependencies)
TRUNCATE TABLE meal_record CASCADE;
TRUNCATE TABLE standard_options CASCADE;
TRUNCATE TABLE meal_type CASCADE;
TRUNCATE TABLE diet_plan CASCADE;

-- Reset sequences to restart from 1
ALTER SEQUENCE diet_plan_id_seq RESTART WITH 1;
ALTER SEQUENCE meal_type_id_seq RESTART WITH 1;
ALTER SEQUENCE standard_options_id_seq RESTART WITH 1;
ALTER SEQUENCE meal_record_id_seq RESTART WITH 1;
