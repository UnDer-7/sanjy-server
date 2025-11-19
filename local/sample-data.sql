-- =============================================
-- SAMPLE DATA SCRIPT - DIET CONTROL DATABASE
-- Purpose: Generate realistic test data
-- =============================================
-- Clean existing data (in reverse order of dependencies)

TRUNCATE TABLE
    meal_record CASCADE;

TRUNCATE TABLE
    standard_options CASCADE;

TRUNCATE TABLE
    meal_type CASCADE;

TRUNCATE TABLE
    diet_plan CASCADE;

-- Reset sequences
ALTER SEQUENCE diet_plan_id_seq RESTART WITH 1;

ALTER SEQUENCE meal_type_id_seq RESTART WITH 1;

ALTER SEQUENCE standard_options_id_seq RESTART WITH 1;

ALTER SEQUENCE meal_record_id_seq RESTART WITH 1;

-- =============================================
-- INSERT DIET PLAN
-- =============================================
INSERT
    INTO
        diet_plan(
            name,
            start_date,
            end_date,
            is_active,
            daily_calories,
            daily_protein_g,
            daily_carbs_g,
            daily_fat_g,
            goal,
            nutritionist_notes,
            created_at
        )
    VALUES(
        'Plan N°02 - Cutting',
        CURRENT_DATE - INTERVAL '27 days',
        CURRENT_DATE + INTERVAL '60 days',
        TRUE,
        2266,
        186,
        288,
        30,
        'Body fat reduction with muscle mass preservation',
        'Patient has lactose intolerance. Avoid dairy products. Drink at least 2.5L of water daily.',
        CURRENT_TIMESTAMP - INTERVAL '27 days'
    );

-- =============================================
-- INSERT MEAL TYPES
-- =============================================
-- Breakfast (id: 1)
INSERT
    INTO
        meal_type(
            diet_plan_id,
            name,
            observation,
            scheduled_time
        )
    VALUES(
        1,
        'Breakfast',
        '45g protein | 35g carbs | 6g fat | 380 kcal',
        '09:30:00'
    );

-- Pre-workout snack (id: 2)
INSERT
    INTO
        meal_type(
            diet_plan_id,
            name,
            observation,
            scheduled_time
        )
    VALUES(
        1,
        'Pre-workout snack',
        '25g protein | 40g carbs | 3g fat | 285 kcal',
        '06:20:00'
    );

-- Lunch (id: 3)
INSERT
    INTO
        meal_type(
            diet_plan_id,
            name,
            observation,
            scheduled_time
        )
    VALUES(
        1,
        'Lunch',
        '50g protein | 80g carbs | 8g fat | 600 kcal',
        '12:30:00'
    );

-- Afternoon snack (id: 4)
INSERT
    INTO
        meal_type(
            diet_plan_id,
            name,
            observation,
            scheduled_time
        )
    VALUES(
        1,
        'Afternoon snack',
        '20g protein | 30g carbs | 5g fat | 250 kcal',
        '16:00:00'
    );

-- Dinner (id: 5)
INSERT
    INTO
        meal_type(
            diet_plan_id,
            name,
            observation,
            scheduled_time
        )
    VALUES(
        1,
        'Dinner',
        '45g protein | 30g carbs | 5g fat | 360 kcal',
        '21:00:00'
    );

-- Evening snack (id: 6)
INSERT
    INTO
        meal_type(
            diet_plan_id,
            name,
            observation,
            scheduled_time
        )
    VALUES(
        1,
        'Evening snack',
        '20g protein | 15g carbs | 3g fat | 170 kcal',
        '23:30:00'
    );

-- =============================================
-- INSERT STANDARD OPTIONS
-- =============================================
-- Breakfast options
INSERT
    INTO
        standard_options(
            meal_type_id,
            option_number,
            description
        )
    VALUES(
        1,
        1,
        'French bread without crumb -- 45g | Scrambled eggs -- 3 eggs (150g) | Zero lactose fresh minas cheese -- 25g'
    ),
    (
        1,
        2,
        'Scrambled eggs -- 2 whole eggs (100g) | Whole wheat bread -- 25g (half large slice or 1 small slice) | Natural juice (no sugar) -- 180ml'
    ),
    (
        1,
        3,
        'Tapioca -- 50g | Shredded chicken breast -- 80g | Tomato -- 50g | Zero lactose cottage cheese -- 30g'
    );

-- Pre-workout snack options
INSERT
    INTO
        standard_options(
            meal_type_id,
            option_number,
            description
        )
    VALUES(
        2,
        1,
        'Banana -- 1 unit (90g) | Whey protein isolate -- 30g | Oats -- 20g'
    ),
    (
        2,
        2,
        'Sweet potato -- 150g | Chicken breast -- 80g'
    ),
    (
        2,
        3,
        'White rice -- 40g (cooked) | Egg whites -- 4 units (120g)'
    );

-- Lunch options
INSERT
    INTO
        standard_options(
            meal_type_id,
            option_number,
            description
        )
    VALUES(
        3,
        1,
        'Grilled chicken breast -- 150g | Brown rice -- 100g (cooked) | Black beans -- 80g | Mixed salad -- 100g | Olive oil -- 5g'
    ),
    (
        3,
        2,
        'Baked tilapia -- 180g | Sweet potato -- 200g | Steamed broccoli -- 150g | Olive oil -- 5g'
    ),
    (
        3,
        3,
        'Lean ground beef (patinho) -- 150g | White rice -- 100g (cooked) | Sautéed vegetables -- 150g | Tomato sauce -- 30g'
    );

-- Afternoon snack options
INSERT
    INTO
        standard_options(
            meal_type_id,
            option_number,
            description
        )
    VALUES(
        4,
        1,
        'Rice crackers -- 30g | Turkey breast -- 50g | Apple -- 1 unit (120g)'
    ),
    (
        4,
        2,
        'Natural yogurt zero lactose -- 150g | Granola -- 25g | Strawberries -- 80g'
    ),
    (
        4,
        3,
        'Whole wheat bread -- 25g | Tuna in water -- 80g | Lettuce and tomato -- 50g'
    );

-- Dinner options
INSERT
    INTO
        standard_options(
            meal_type_id,
            option_number,
            description
        )
    VALUES(
        5,
        1,
        'Cooked pasta -- 90g | Ground beef (patinho) -- 120g | Homemade tomato sauce -- 50g | Steamed broccoli -- 100g'
    ),
    (
        5,
        2,
        'Grilled chicken -- 140g | Mashed potato or sweet potato (no milk) -- 130g or 170g | Mixed vegetables -- 100g | Olive oil -- 5g'
    ),
    (
        5,
        3,
        'Grilled salmon -- 150g | Quinoa -- 80g (cooked) | Asparagus -- 120g | Lemon -- 1/2 unit'
    );

-- Evening snack options
INSERT
    INTO
        standard_options(
            meal_type_id,
            option_number,
            description
        )
    VALUES(
        6,
        1,
        'Casein protein -- 30g | Peanut butter -- 10g'
    ),
    (
        6,
        2,
        'Zero lactose cottage cheese -- 100g | Almonds -- 15g'
    ),
    (
        6,
        3,
        'Scrambled egg whites -- 3 units (90g) | Cherry tomatoes -- 50g'
    );

-- =============================================
-- INSERT MEAL RECORDS (~110 records)
-- Distribution: ~27 days with 4-6 meals per day
-- Mix of planned and free meals
-- Days -26 and -25 have MORE free meals than planned
-- =============================================
-- Day -26 (5 meals - 3 free, 2 planned) - First days of diet plan, still adjusting
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '26 days 14 hours 45 minutes',
        1,
        TRUE,
        NULL,
        'Pancakes with syrup -- 3 units | Bacon -- 4 strips | Orange juice -- 250ml',
        1.0,
        'combo',
        'Weekend brunch - First day of diet plan, still adjusting',
        CURRENT_TIMESTAMP - INTERVAL '26 days 14 hours 45 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '26 days 11 hours 20 minutes',
        3,
        TRUE,
        NULL,
        'BBQ ribs -- 300g | Coleslaw salad -- 100g | Beer (light) -- 350ml',
        1.0,
        'plate',
        'Family BBQ',
        CURRENT_TIMESTAMP - INTERVAL '26 days 11 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '26 days 8 hours 30 minutes',
        4,
        TRUE,
        NULL,
        'Chips -- 50g | Cheese dip -- 30g',
        1.0,
        'snack',
        'Watching sports',
        CURRENT_TIMESTAMP - INTERVAL '26 days 8 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '26 days 3 hours 15 minutes',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        'Back on track for dinner',
        CURRENT_TIMESTAMP - INTERVAL '26 days 3 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '26 days 40 minutes',
        6,
        FALSE,
        16,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '26 days 40 minutes'
    );

-- Day -25 (6 meals - 4 free, 2 planned)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '25 days 14 hours 50 minutes',
        1,
        TRUE,
        NULL,
        'Croissant -- 2 units | Cappuccino with whole milk -- 300ml | Strawberry jam -- 20g',
        2.0,
        'units',
        'Cafe breakfast',
        CURRENT_TIMESTAMP - INTERVAL '25 days 14 hours 50 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '25 days 11 hours 10 minutes',
        3,
        TRUE,
        NULL,
        'Pasta carbonara -- 350g | Garlic bread -- 2 slices | Caesar salad -- 150g',
        1.0,
        'plate',
        'Italian restaurant',
        CURRENT_TIMESTAMP - INTERVAL '25 days 11 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '25 days 8 hours 45 minutes',
        4,
        TRUE,
        NULL,
        'Brownie -- 1 large piece (80g) | Latte -- 240ml',
        1.0,
        'piece',
        'Coffee break treat',
        CURRENT_TIMESTAMP - INTERVAL '25 days 8 hours 45 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '25 days 5 hours 30 minutes',
        4,
        TRUE,
        NULL,
        'Popcorn (butter flavor) -- 100g | Soda -- 350ml',
        1.0,
        'bag',
        'Movie theater snack',
        CURRENT_TIMESTAMP - INTERVAL '25 days 5 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '25 days 2 hours 20 minutes',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        'Light dinner to compensate',
        CURRENT_TIMESTAMP - INTERVAL '25 days 2 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '25 days 25 minutes',
        6,
        FALSE,
        17,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '25 days 25 minutes'
    );

-- Day -24 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '24 days 2 hours 30 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        'Getting back to routine',
        CURRENT_TIMESTAMP - INTERVAL '24 days 2 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '24 days 9 hours',
        3,
        FALSE,
        7,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '24 days 9 hours'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '24 days 12 hours',
        4,
        FALSE,
        10,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '24 days 12 hours'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '24 days 17 hours',
        5,
        FALSE,
        13,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '24 days 17 hours'
    );

-- Day -23 (5 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '23 days 18 hours 40 minutes',
        2,
        FALSE,
        4,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '23 days 18 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '23 days 14 hours 30 minutes',
        1,
        FALSE,
        2,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '23 days 14 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '23 days 11 hours 30 minutes',
        3,
        FALSE,
        8,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '23 days 11 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '23 days 8 hours',
        4,
        FALSE,
        11,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '23 days 8 hours'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '23 days 3 hours',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '23 days 3 hours'
    );

-- Day -22 (6 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '22 days 18 hours 20 minutes',
        2,
        FALSE,
        5,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '22 days 18 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '22 days 14 hours 15 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '22 days 14 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '22 days 11 hours 45 minutes',
        3,
        FALSE,
        9,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '22 days 11 hours 45 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '22 days 8 hours 10 minutes',
        4,
        TRUE,
        NULL,
        'Protein bar -- 1 unit (60g)',
        1.0,
        'unit',
        'On the go',
        CURRENT_TIMESTAMP - INTERVAL '22 days 8 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '22 days 3 hours 15 minutes',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '22 days 3 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '22 days 30 minutes',
        6,
        FALSE,
        16,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '22 days 30 minutes'
    );

-- Day -21 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '21 days 14 hours 20 minutes',
        1,
        FALSE,
        3,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '21 days 14 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '21 days 11 hours 40 minutes',
        3,
        FALSE,
        7,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '21 days 11 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '21 days 7 hours 50 minutes',
        4,
        FALSE,
        12,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '21 days 7 hours 50 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '21 days 2 hours 45 minutes',
        5,
        FALSE,
        13,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '21 days 2 hours 45 minutes'
    );

-- Day -20 (5 meals - with free meal)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '20 days 18 hours 25 minutes',
        2,
        FALSE,
        6,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '20 days 18 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '20 days 14 hours 35 minutes',
        1,
        FALSE,
        2,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '20 days 14 hours 35 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '20 days 11 hours 20 minutes',
        3,
        TRUE,
        NULL,
        'Pizza slice -- 2 slices | Diet soda -- 350ml',
        2.0,
        'slices',
        'Birthday celebration at work',
        CURRENT_TIMESTAMP - INTERVAL '20 days 11 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '20 days 8 hours 15 minutes',
        4,
        FALSE,
        10,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '20 days 8 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '20 days 3 hours 30 minutes',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '20 days 3 hours 30 minutes'
    );

-- Day -19 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '19 days 14 hours 25 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '19 days 14 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '19 days 11 hours 35 minutes',
        3,
        FALSE,
        8,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '19 days 11 hours 35 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '19 days 7 hours 55 minutes',
        4,
        FALSE,
        11,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '19 days 7 hours 55 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '19 days 2 hours 50 minutes',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '19 days 2 hours 50 minutes'
    );

-- Day -18 (5 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '18 days 18 hours 15 minutes',
        2,
        FALSE,
        4,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '18 days 18 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '18 days 14 hours 40 minutes',
        1,
        FALSE,
        3,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '18 days 14 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '18 days 11 hours 25 minutes',
        3,
        FALSE,
        9,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '18 days 11 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '18 days 7 hours 45 minutes',
        4,
        FALSE,
        12,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '18 days 7 hours 45 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '18 days 2 hours 35 minutes',
        5,
        FALSE,
        13,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '18 days 2 hours 35 minutes'
    );

-- Day -17 (6 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '17 days 18 hours 30 minutes',
        2,
        FALSE,
        5,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '17 days 18 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '17 days 14 hours 20 minutes',
        1,
        FALSE,
        2,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '17 days 14 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '17 days 11 hours 30 minutes',
        3,
        FALSE,
        7,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '17 days 11 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '17 days 8 hours 5 minutes',
        4,
        FALSE,
        10,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '17 days 8 hours 5 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '17 days 3 hours 10 minutes',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '17 days 3 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '17 days 25 minutes',
        6,
        FALSE,
        17,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '17 days 25 minutes'
    );

-- Day -16 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '16 days 14 hours 30 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '16 days 14 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '16 days 11 hours 20 minutes',
        3,
        FALSE,
        8,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '16 days 11 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '16 days 8 hours 10 minutes',
        4,
        TRUE,
        NULL,
        'Granola bar -- 1 unit (30g) | Coffee with almond milk -- 200ml',
        1.0,
        'unit',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '16 days 8 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '16 days 3 hours',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '16 days 3 hours'
    );

-- Day -15 (5 meals - with free meal)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '15 days 18 hours 10 minutes',
        2,
        FALSE,
        6,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '15 days 18 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '15 days 14 hours 15 minutes',
        1,
        FALSE,
        3,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '15 days 14 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '15 days 11 hours 40 minutes',
        3,
        FALSE,
        9,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '15 days 11 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '15 days 7 hours 30 minutes',
        4,
        TRUE,
        NULL,
        'Açaí bowl (small) -- 200ml | Banana -- 1 unit',
        1.0,
        'bowl',
        'Craving something sweet',
        CURRENT_TIMESTAMP - INTERVAL '15 days 7 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '15 days 2 hours 40 minutes',
        5,
        FALSE,
        13,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '15 days 2 hours 40 minutes'
    );

-- Day -14 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '14 days 14 hours 25 minutes',
        1,
        FALSE,
        2,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '14 days 14 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '14 days 11 hours 35 minutes',
        3,
        FALSE,
        7,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '14 days 11 hours 35 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '14 days 8 hours 20 minutes',
        4,
        FALSE,
        11,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '14 days 8 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '14 days 3 hours 15 minutes',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '14 days 3 hours 15 minutes'
    );

-- Day -13 (6 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '13 days 18 hours 20 minutes',
        2,
        FALSE,
        4,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '13 days 18 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '13 days 14 hours 30 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '13 days 14 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '13 days 11 hours 25 minutes',
        3,
        FALSE,
        8,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '13 days 11 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '13 days 8 hours 15 minutes',
        4,
        FALSE,
        12,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '13 days 8 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '13 days 3 hours 5 minutes',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '13 days 3 hours 5 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '13 days 35 minutes',
        6,
        FALSE,
        18,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '13 days 35 minutes'
    );

-- Day -12 (5 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '12 days 18 hours 15 minutes',
        2,
        FALSE,
        5,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '12 days 18 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '12 days 14 hours 40 minutes',
        1,
        FALSE,
        3,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '12 days 14 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '12 days 11 hours 30 minutes',
        3,
        FALSE,
        9,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '12 days 11 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '12 days 7 hours 50 minutes',
        4,
        FALSE,
        10,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '12 days 7 hours 50 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '12 days 2 hours 55 minutes',
        5,
        FALSE,
        13,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '12 days 2 hours 55 minutes'
    );

-- Day -11 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '11 days 14 hours 20 minutes',
        1,
        FALSE,
        2,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '11 days 14 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '11 days 11 hours 40 minutes',
        3,
        FALSE,
        7,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '11 days 11 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '11 days 8 hours 5 minutes',
        4,
        FALSE,
        11,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '11 days 8 hours 5 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '11 days 3 hours 20 minutes',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '11 days 3 hours 20 minutes'
    );

-- Day -10 (5 meals - with free meal)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '10 days 18 hours 25 minutes',
        2,
        FALSE,
        6,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '10 days 18 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '10 days 14 hours 35 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '10 days 14 hours 35 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '10 days 11 hours 15 minutes',
        3,
        TRUE,
        NULL,
        'Cheeseburger -- 1 unit | French fries -- 100g | Diet coke -- 350ml',
        1.0,
        'combo',
        'Weekend cheat meal',
        CURRENT_TIMESTAMP - INTERVAL '10 days 11 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '10 days 7 hours 40 minutes',
        4,
        FALSE,
        12,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '10 days 7 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '10 days 2 hours 30 minutes',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '10 days 2 hours 30 minutes'
    );

-- Day -9 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '9 days 14 hours 30 minutes',
        1,
        FALSE,
        3,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '9 days 14 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '9 days 11 hours 25 minutes',
        3,
        FALSE,
        8,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '9 days 11 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '9 days 8 hours 15 minutes',
        4,
        FALSE,
        10,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '9 days 8 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '9 days 3 hours 10 minutes',
        5,
        FALSE,
        13,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '9 days 3 hours 10 minutes'
    );

-- Day -8 (6 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '8 days 18 hours 10 minutes',
        2,
        FALSE,
        4,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '8 days 18 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '8 days 14 hours 25 minutes',
        1,
        FALSE,
        2,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '8 days 14 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '8 days 11 hours 35 minutes',
        3,
        FALSE,
        9,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '8 days 11 hours 35 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '8 days 7 hours 55 minutes',
        4,
        FALSE,
        11,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '8 days 7 hours 55 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '8 days 2 hours 45 minutes',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '8 days 2 hours 45 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '8 days 20 minutes',
        6,
        FALSE,
        16,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '8 days 20 minutes'
    );

-- Day -7 (5 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '7 days 18 hours 30 minutes',
        2,
        FALSE,
        5,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '7 days 18 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '7 days 14 hours 20 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '7 days 14 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '7 days 11 hours 30 minutes',
        3,
        FALSE,
        7,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '7 days 11 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '7 days 8 hours 10 minutes',
        4,
        FALSE,
        12,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '7 days 8 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '7 days 3 hours 5 minutes',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '7 days 3 hours 5 minutes'
    );

-- Day -6 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '6 days 14 hours 35 minutes',
        1,
        FALSE,
        3,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '6 days 14 hours 35 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '6 days 11 hours 20 minutes',
        3,
        FALSE,
        8,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '6 days 11 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '6 days 7 hours 45 minutes',
        4,
        TRUE,
        NULL,
        'Chocolate cookie -- 2 units (60g) | Black coffee -- 200ml',
        2.0,
        'units',
        'Office meeting snacks',
        CURRENT_TIMESTAMP - INTERVAL '6 days 7 hours 45 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '6 days 2 hours 50 minutes',
        5,
        FALSE,
        13,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '6 days 2 hours 50 minutes'
    );

-- Day -5 (5 meals - with free meal)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '5 days 18 hours 15 minutes',
        2,
        FALSE,
        6,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '5 days 18 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '5 days 14 hours 25 minutes',
        1,
        FALSE,
        2,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '5 days 14 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '5 days 11 hours 40 minutes',
        3,
        FALSE,
        9,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '5 days 11 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '5 days 8 hours 5 minutes',
        4,
        TRUE,
        NULL,
        'Ice cream (sugar-free) -- 100ml',
        1.0,
        'serving',
        'Dessert after good workout',
        CURRENT_TIMESTAMP - INTERVAL '5 days 8 hours 5 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '5 days 3 hours 15 minutes',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '5 days 3 hours 15 minutes'
    );

-- Day -4 (6 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '4 days 18 hours 20 minutes',
        2,
        FALSE,
        4,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '4 days 18 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '4 days 14 hours 30 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '4 days 14 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '4 days 11 hours 25 minutes',
        3,
        FALSE,
        7,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '4 days 11 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '4 days 8 hours 15 minutes',
        4,
        FALSE,
        10,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '4 days 8 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '4 days 3 hours 10 minutes',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '4 days 3 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '4 days 30 minutes',
        6,
        FALSE,
        17,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '4 days 30 minutes'
    );

-- Day -3 (4 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '3 days 14 hours 20 minutes',
        1,
        FALSE,
        3,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '3 days 14 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '3 days 11 hours 35 minutes',
        3,
        FALSE,
        8,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '3 days 11 hours 35 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '3 days 7 hours 50 minutes',
        4,
        FALSE,
        11,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '3 days 7 hours 50 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '3 days 2 hours 40 minutes',
        5,
        FALSE,
        13,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '3 days 2 hours 40 minutes'
    );

-- Day -2 (5 meals - with free meal)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '2 days 18 hours 25 minutes',
        2,
        FALSE,
        5,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '2 days 18 hours 25 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '2 days 14 hours 30 minutes',
        1,
        FALSE,
        2,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '2 days 14 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '2 days 11 hours 15 minutes',
        3,
        TRUE,
        NULL,
        'Japanese food -- Sashimi (150g) | Brown rice -- 100g | Seaweed salad -- 80g',
        1.0,
        'combo',
        'Business lunch',
        CURRENT_TIMESTAMP - INTERVAL '2 days 11 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '2 days 8 hours 20 minutes',
        4,
        FALSE,
        12,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '2 days 8 hours 20 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '2 days 3 hours 5 minutes',
        5,
        FALSE,
        14,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '2 days 3 hours 5 minutes'
    );

-- Day -1 (yesterday - 6 meals)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '1 day 18 hours 10 minutes',
        2,
        FALSE,
        6,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '1 day 18 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '1 day 14 hours 35 minutes',
        1,
        FALSE,
        1,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '1 day 14 hours 35 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '1 day 11 hours 30 minutes',
        3,
        FALSE,
        9,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '1 day 11 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '1 day 8 hours 10 minutes',
        4,
        FALSE,
        10,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '1 day 8 hours 10 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours 15 minutes',
        5,
        FALSE,
        15,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours 15 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '1 day 45 minutes',
        6,
        FALSE,
        18,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '1 day 45 minutes'
    );

-- Day 0 (today - 4 meals so far)
INSERT
    INTO
        meal_record(
            consumed_at,
            meal_type_id,
            is_free_meal,
            standard_option_id,
            free_meal_description,
            quantity,
            unit,
            notes,
            created_at
        )
    VALUES(
        CURRENT_TIMESTAMP - INTERVAL '5 hours 40 minutes',
        2,
        FALSE,
        4,
        NULL,
        1.0,
        'serving',
        'Good energy for workout',
        CURRENT_TIMESTAMP - INTERVAL '5 hours 40 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '2 hours 30 minutes',
        1,
        FALSE,
        3,
        NULL,
        1.0,
        'serving',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '2 hours 30 minutes'
    ),
    (
        CURRENT_TIMESTAMP - INTERVAL '30 minutes',
        3,
        FALSE,
        7,
        NULL,
        1.0,
        'serving',
        'Feeling satisfied',
        CURRENT_TIMESTAMP - INTERVAL '30 minutes'
    );
