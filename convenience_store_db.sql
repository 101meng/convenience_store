/*
 Navicat Premium Data Transfer

 Source Server         : a
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : convenience_store_db

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 16/05/2026 19:11:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for banners
-- ----------------------------
DROP TABLE IF EXISTS `banners`;
CREATE TABLE `banners`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片的网络链接',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '点击跳转的App路由，如 product_detail/1',
  `sort_order` int(0) NULL DEFAULT 0 COMMENT '排序权重，数字越大越靠前',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否展示：1展示，0下架',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banners
-- ----------------------------
INSERT INTO `banners` VALUES (1, 'http://10.0.2.2:8080/images/banner1.png', 'category/1', 10, 1, '2026-05-04 15:31:01');
INSERT INTO `banners` VALUES (2, 'http://10.0.2.2:8080/images/banner2.png', 'product_detail/9', 5, 1, '2026-05-04 15:31:01');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `cart_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NULL DEFAULT NULL,
  `store_id` int(0) NOT NULL DEFAULT 1 COMMENT '门店ID',
  `product_id` int(0) NULL DEFAULT NULL,
  `quantity` int(0) NULL DEFAULT 1,
  PRIMARY KEY (`cart_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (38, 1, 2, 5, 1);

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `category_id` int(0) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, 'Fresh Bento', 'icon_fresh.png');
INSERT INTO `categories` VALUES (2, 'Snacks', 'icon_snacks.png');
INSERT INTO `categories` VALUES (3, 'Drinks', 'icon_drinks.png');
INSERT INTO `categories` VALUES (4, 'Bakery', 'icon_bakery.png');
INSERT INTO `categories` VALUES (5, 'Desserts', 'icon_desserts.png');
INSERT INTO `categories` VALUES (6, 'Dairy & Chilled', 'icon_dairy.png');
INSERT INTO `categories` VALUES (7, 'Instant Food', 'icon_instant.png');
INSERT INTO `categories` VALUES (8, 'Personal Care', 'icon_care.png');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `item_id` int(0) NOT NULL AUTO_INCREMENT,
  `order_id` int(0) NULL DEFAULT NULL,
  `product_id` int(0) NULL DEFAULT NULL,
  `quantity` int(0) NOT NULL,
  `price_at_time` decimal(10, 2) NOT NULL,
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 1, 1, 12.50);
INSERT INTO `order_items` VALUES (2, 1, 9, 1, 4.50);
INSERT INTO `order_items` VALUES (3, 2, 9, 1, 4.50);
INSERT INTO `order_items` VALUES (19, 10, 3, 3, 15.90);
INSERT INTO `order_items` VALUES (20, 11, 3, 4, 15.90);
INSERT INTO `order_items` VALUES (21, 11, 2, 1, 9.90);
INSERT INTO `order_items` VALUES (22, 11, 7, 1, 0.00);
INSERT INTO `order_items` VALUES (23, 11, 9, 1, 0.00);
INSERT INTO `order_items` VALUES (24, 11, 32, 1, 0.00);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `order_id` int(0) NOT NULL AUTO_INCREMENT,
  `order_sn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` int(0) NULL DEFAULT NULL,
  `store_id` int(0) NULL DEFAULT NULL,
  `total_amount` decimal(10, 2) NOT NULL,
  `delivery_fee` decimal(10, 2) NULL DEFAULT 0.00,
  `actual_amount` decimal(10, 2) NOT NULL,
  `order_type` enum('shipping','pickup') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配送或自提',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式：wechat/alipay/apple_pay',
  `status` enum('pending','delivering','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending',
  `delivery_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '下单时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_sn`(`order_sn`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `store_id`(`store_id`) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD-2026-A1B2C3D4', 1, NULL, 17.00, 1.50, 18.50, 'shipping', 'WeChat Pay', 'completed', 'Central Park West, NY 10025', '2026-05-01 10:30:00');
INSERT INTO `orders` VALUES (2, 'ORD-2026-E5F6G7H8', 1, 1, 4.50, 0.00, 4.50, 'pickup', 'Apple Pay', 'completed', NULL, '2026-05-03 14:15:00');
INSERT INTO `orders` VALUES (10, 'ORD-2026-E09D8982', 1, 1, 47.70, 0.00, 47.70, 'pickup', 'WeChat Pay', 'completed', NULL, '2026-05-16 14:38:19');
INSERT INTO `orders` VALUES (11, 'ORD-2026-CD9EF1EF', 1, 1, 73.50, 0.00, 73.50, 'pickup', 'wechat', 'completed', NULL, '2026-05-16 17:59:27');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `product_id` int(0) NOT NULL AUTO_INCREMENT,
  `category_id` int(0) NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `is_flash_sale` tinyint(1) NULL DEFAULT 0 COMMENT '是否为秒杀商品 1是 0否',
  `flash_sale_end_time` datetime(0) NULL DEFAULT NULL COMMENT '秒杀结束时间',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '如：1L, 250g',
  `calories` int(0) NULL DEFAULT NULL,
  `protein` int(0) NULL DEFAULT NULL,
  `total_fat` int(0) NULL DEFAULT NULL,
  `shelf_life` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tag1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定义标签1',
  `tag2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定义标签2',
  `tag3` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定义标签3',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `category_id`(`category_id`) USING BTREE,
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 1, 'Grilled Chicken Quinoa Bowl', 'High-protein grilled chicken breast with organic quinoa, roasted sweet potatoes, and a light lemon tahini dressing.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/1-1.png', '1 Bowl', 420, 35, 12, '2 Days', 'High Protein', 'Healthy', 'Low Carb', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (2, 1, 'Vegan Buddha Bowl', 'A vibrant mix of avocado, chickpeas, shredded carrots, and mixed greens. Perfect for a clean eating day.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/1-2.png', '1 Bowl', 380, 14, 18, '2 Days', 'Vegan', 'Organic', 'Fiber', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (3, 1, 'Premium Salmon Sushi Box', 'Freshly made salmon nigiri and avocado rolls. Served with low-sodium soy sauce and organic wasabi.', 19.99, 1, '2026-12-31 23:59:59', 'http://10.0.2.2:8080/images/1-3.png', '1 Box', 520, 28, 15, '1 Day', 'Seafood', 'Fresh', 'Omega-3', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (4, 1, 'Teriyaki Beef & Rice', 'Tender beef slices glazed in homemade teriyaki sauce over fluffy jasmine rice and steamed broccoli.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/1-4.png', '1 Bento', 650, 32, 22, '2 Days', 'Hot Food', 'Filling', 'Asian', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (5, 2, 'Sea Salt Potato Chips', 'Thick-cut, kettle-cooked potato chips seasoned with natural sea salt. Dangerously addictive.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/2-1.png', '150g', 540, 6, 32, '6 Months', 'Crunchy', 'Salty', 'Guilty Pleasure', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (6, 2, 'Black Pepper Beef Jerky', 'Hickory smoked beef jerky with a serious kick of black pepper. The ultimate late-night coding snack.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/2-2.png', '100g', 280, 45, 8, '8 Months', 'High Protein', 'Spicy', 'Energy', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (7, 2, 'Roasted Mixed Nuts', 'A premium blend of almonds, walnuts, and cashews lightly roasted without extra oil.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/2-3.png', '200g', 620, 20, 52, '12 Months', 'Keto', 'Vegan', 'Healthy Fats', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (8, 2, 'Double Chocolate Protein Bar', 'Packed with 20g of whey protein. Tastes like a brownie but fuels your muscles.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/2-4.png', '60g', 220, 20, 8, '9 Months', 'Fitness', 'Muscle', 'Low Sugar', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (9, 3, 'Artisanal Cold Brew', 'Steeped for 18 hours for a remarkably smooth, low-acid coffee experience. Zero calories.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/3-1.png', '400ml', 5, 0, 0, '5 Days', 'Caffeine', 'Smooth', 'Sugar Free', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (10, 3, 'Iced Matcha Latte', 'Ceremonial grade matcha blended with creamy oat milk. A gentle energy boost.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/3-2.png', '500ml', 180, 4, 6, '2 Days', 'Dairy-Free', 'Antioxidants', 'Zen', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (11, 3, 'Sparkling Peach Water', 'Refreshing carbonated water infused with real peach essence. Zero sugar, zero guilt.', 3.50, 1, '2026-12-31 23:59:59', 'http://10.0.2.2:8080/images/3-3.png', '330ml', 0, 0, 0, '12 Months', 'Zero Sugar', 'Fizzy', 'Refreshing', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (12, 3, 'Cold Pressed Green Juice', '100% organic kale, spinach, cucumber, and green apple. A liquid salad.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/3-4.png', '350ml', 110, 2, 0, '3 Days', 'Detox', 'Organic', 'Vitamins', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (13, 4, 'Classic Butter Croissant', 'Flaky, buttery, and baked fresh daily. The perfect companion for your morning coffee.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/4-1.png', '1 Piece', 320, 6, 18, '2 Days', 'Baked Daily', 'Flaky', 'Breakfast', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (14, 4, 'Artisan Sourdough Loaf', 'Naturally fermented sourdough with a crusty exterior and soft, chewy interior.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/4-2.png', '1 Loaf', 850, 24, 8, '5 Days', 'Artisanal', 'Fermented', 'Carbs', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (15, 4, 'Blueberry Muffin', 'Moist muffin loaded with fresh blueberries and topped with a sugar crumble.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/4-3.png', '1 Piece', 420, 5, 16, '3 Days', 'Sweet', 'Berry', 'Treat', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (16, 5, 'Strawberry Cheesecake', 'Classic New York style cheesecake topped with fresh strawberry glaze.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/5-1.png', '1 Slice', 480, 8, 30, '4 Days', 'Decadent', 'Sweet', 'Indulgence', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (17, 5, 'Matcha Tiramisu', 'A Japanese twist on the Italian classic, featuring matcha-infused mascarpone cream.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/5-2.png', '1 Slice', 460, 7, 28, '2 Days', 'Fusion', 'Creamy', 'Matcha', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (18, 1, 'Classic Tonkatsu Bento', 'Crispy deep-fried pork cutlet served with shredded cabbage and steamed rice.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/1-5.png', '1 Bento', 780, 25, 35, '1 Day', 'Japanese', 'Fried', 'Meat', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (19, 1, 'Spicy Tuna Poke Bowl', 'Fresh raw tuna chunks in a spicy mayo sauce over rice with edamame and seaweed.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/1-6.png', '1 Bowl', 450, 30, 15, '1 Day', 'Seafood', 'Spicy', 'Fresh', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (20, 1, 'Mushroom Truffle Risotto', 'Creamy Italian rice dish cooked with wild mushrooms and finished with truffle oil.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/1-7.png', '1 Bowl', 520, 12, 22, '2 Days', 'Vegetarian', 'Gourmet', 'Comfort Food', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (21, 2, 'Spicy Nacho Chips', 'Crunchy corn tortilla chips generously coated in spicy nacho cheese seasoning.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/2-5.png', '150g', 500, 5, 25, '6 Months', 'Spicy', 'Crunchy', 'Party', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (22, 2, 'Fruity Gummy Bears', 'Chewy, fruit-flavored gummy candies in various fun shapes and colors.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/2-6.png', '120g', 350, 2, 0, '12 Months', 'Sweet', 'Chewy', 'Candy', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (23, 2, 'Dark Chocolate Almonds', 'Premium whole almonds coated in rich, slightly bitter 70% dark chocolate.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/2-7.png', '100g', 480, 10, 35, '8 Months', 'Chocolate', 'Nutty', 'Antioxidants', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (24, 2, 'Spicy Wasabi Peas', 'Crunchy roasted green peas coated with an intense, nose-clearing wasabi kick.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/2-8.png', '130g', 420, 12, 14, '10 Months', 'Spicy', 'Crunchy', 'Snack', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (25, 3, 'Zero Sugar Cola', 'Classic cola taste without the calories or sugar. Best served chilled.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/3-5.png', '330ml', 0, 0, 0, '12 Months', 'Zero Sugar', 'Soda', 'Refreshing', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (26, 3, 'Electrolyte Sports Drink', 'Citrus-flavored hydration beverage packed with essential electrolytes for recovery.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/3-6.png', '500ml', 80, 0, 0, '12 Months', 'Hydration', 'Energy', 'Sports', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (27, 3, 'Premium Oolong Tea', 'Authentic roasted oolong tea with a floral aroma and smooth finish.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/3-7.png', '450ml', 0, 0, 0, '9 Months', 'Tea', 'Sugar Free', 'Traditional', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (28, 3, 'Oat Milk Latte', 'Espresso perfectly balanced with creamy, plant-based oat milk.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/3-8.png', '350ml', 120, 2, 4, '3 Days', 'Coffee', 'Dairy-Free', 'Morning', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (29, 4, 'Garlic Bread Baguette', 'Crispy mini baguette generously spread with garlic and herb butter.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/4-4.png', '1 Piece', 380, 8, 18, '2 Days', 'Savory', 'Garlic', 'Warm', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (30, 4, 'Cinnamon Roll', 'Soft, fluffy dough swirled with cinnamon sugar and topped with cream cheese icing.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/4-5.png', '1 Piece', 450, 5, 20, '3 Days', 'Sweet', 'Cinnamon', 'Breakfast', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (31, 4, 'Cheese Danish', 'Flaky pastry filled with sweet, creamy cheese and glazed with light syrup.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/4-6.png', '1 Piece', 360, 6, 22, '2 Days', 'Pastry', 'Cheese', 'Flaky', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (32, 5, 'Vanilla Bean Macarons', 'Delicate French almond meringue cookies filled with rich vanilla buttercream.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/5-3.png', '3 Pieces', 210, 4, 10, '5 Days', 'French', 'Sweet', 'Delicate', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (33, 5, 'Chocolate Lava Cake', 'Rich chocolate cake with a molten, gooey chocolate center. Warm before eating.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/5-4.png', '1 Cake', 550, 6, 30, '4 Days', 'Chocolate', 'Warm', 'Indulgent', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (34, 6, 'Blueberry Greek Yogurt', 'Thick, high-protein Greek yogurt blended with real blueberry preserve.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/6-1.png', '150g', 120, 12, 0, '14 Days', 'Probiotic', 'Healthy', 'Breakfast', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (35, 6, 'Fresh Whole Milk', 'Farm-fresh pasteurized whole milk, rich in calcium and vitamin D.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/6-2.png', '1L', 600, 32, 32, '7 Days', 'Dairy', 'Calcium', 'Fresh', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (36, 6, 'Cheddar Cheese Slices', 'Aged cheddar cheese, perfectly sliced for sandwiches and burgers.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/6-3.png', '200g', 800, 48, 66, '30 Days', 'Cheese', 'Savory', 'Sandwich', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (37, 6, 'Probiotic Dairy Drink', 'Sweet and tangy fermented milk drink containing active probiotics for gut health.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/6-4.png', '5 Pack', 250, 6, 0, '21 Days', 'Probiotic', 'Digestion', 'Daily', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (38, 6, 'Unsweetened Soy Milk', 'Plant-based milk alternative made from non-GMO soybeans with zero added sugar.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/6-5.png', '1L', 330, 28, 18, '10 Days', 'Vegan', 'Dairy-Free', 'Healthy', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (39, 7, 'Spicy Beef Cup Noodles', 'Classic instant noodles with rich, spicy beef broth and dehydrated veggies.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/7-1.png', '1 Cup', 380, 8, 16, '6 Months', 'Instant', 'Spicy', 'Late Night', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (40, 7, 'Seafood Tonkotsu Ramen', 'Premium instant ramen with creamy pork broth, seafood flavors, and thick noodles.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/7-2.png', '1 Bowl', 460, 12, 18, '6 Months', 'Ramen', 'Seafood', 'Filling', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (41, 7, 'Microwave Mac & Cheese', 'Easy-to-prepare macaroni pasta in a velvety, comforting cheese sauce.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/7-3.png', '1 Bowl', 410, 14, 15, '8 Months', 'Comfort Food', 'Cheese', 'Microwave', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (42, 7, 'Creamy Tomato Soup', 'Rich and hearty tomato soup, perfectly seasoned and ready to heat.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/7-4.png', '1 Cup', 210, 4, 8, '12 Months', 'Soup', 'Warm', 'Vegetarian', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (43, 7, 'Self-Heating Hot Pot', 'Spicy Sichuan-style hot pot that cooks itself. Includes meat, veggies, and noodles.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/7-5.png', '1 Pot', 850, 25, 45, '9 Months', 'Spicy', 'Hot Pot', 'Self-Heating', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (44, 8, 'Travel Toothbrush Set', 'Compact folding toothbrush with a mini tube of mint toothpaste.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/8-1.png', '1 Set', NULL, NULL, NULL, 'N/A', 'Travel', 'Hygiene', 'Essentials', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (45, 8, 'Hand Sanitizer Gel', 'Alcohol-based hand sanitizer that kills 99.9% of germs. Contains aloe vera.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/8-2.png', '50ml', NULL, NULL, NULL, '2 Years', 'Hygiene', 'Clean', 'Protection', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (46, 8, 'Pocket Tissues (3-Pack)', 'Soft, durable, and absorbent facial tissues in convenient travel-sized packs.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/8-3.png', '3 Packs', NULL, NULL, NULL, 'N/A', 'Paper', 'Soft', 'Daily', '2026-05-09 13:25:57', '2026-05-09 13:25:57');
INSERT INTO `products` VALUES (47, 8, 'Moisturizing Lip Balm', 'Hydrating lip care with shea butter and SPF 15 to prevent chapped lips.', NULL, 0, NULL, 'http://10.0.2.2:8080/images/8-4.png', '1 Stick', NULL, NULL, NULL, '3 Years', 'Skincare', 'Moisture', 'Winter', '2026-05-09 13:25:57', '2026-05-09 13:25:57');

-- ----------------------------
-- Table structure for store_products
-- ----------------------------
DROP TABLE IF EXISTS `store_products`;
CREATE TABLE `store_products`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `store_id` int(0) NOT NULL COMMENT '门店ID',
  `product_id` int(0) NOT NULL COMMENT '商品ID',
  `store_price` decimal(10, 2) NOT NULL COMMENT '该门店特有售价',
  `stock` int(0) NOT NULL DEFAULT 0 COMMENT '该门店独立库存',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '在该门店的上架状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_store_product`(`store_id`, `product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store_products
-- ----------------------------
INSERT INTO `store_products` VALUES (1, 1, 1, 28.90, 100, 1);
INSERT INTO `store_products` VALUES (2, 1, 2, 32.50, 100, 1);
INSERT INTO `store_products` VALUES (3, 1, 3, 42.00, 100, 1);
INSERT INTO `store_products` VALUES (4, 1, 4, 35.80, 100, 1);
INSERT INTO `store_products` VALUES (5, 1, 18, 38.00, 100, 1);
INSERT INTO `store_products` VALUES (6, 1, 19, 45.00, 100, 1);
INSERT INTO `store_products` VALUES (7, 1, 20, 39.80, 100, 1);
INSERT INTO `store_products` VALUES (8, 1, 5, 9.90, 100, 1);
INSERT INTO `store_products` VALUES (9, 1, 6, 22.50, 100, 1);
INSERT INTO `store_products` VALUES (10, 1, 7, 18.80, 100, 1);
INSERT INTO `store_products` VALUES (11, 1, 8, 15.90, 100, 1);
INSERT INTO `store_products` VALUES (12, 1, 21, 7.90, 100, 1);
INSERT INTO `store_products` VALUES (13, 1, 22, 6.50, 100, 1);
INSERT INTO `store_products` VALUES (14, 1, 23, 14.90, 100, 1);
INSERT INTO `store_products` VALUES (15, 1, 24, 8.80, 100, 1);
INSERT INTO `store_products` VALUES (16, 1, 9, 12.00, 100, 1);
INSERT INTO `store_products` VALUES (17, 1, 10, 19.50, 100, 1);
INSERT INTO `store_products` VALUES (18, 1, 11, 5.50, 100, 1);
INSERT INTO `store_products` VALUES (19, 1, 12, 16.80, 100, 1);
INSERT INTO `store_products` VALUES (20, 1, 25, 3.50, 100, 1);
INSERT INTO `store_products` VALUES (21, 1, 26, 7.50, 100, 1);
INSERT INTO `store_products` VALUES (22, 1, 27, 9.00, 100, 1);
INSERT INTO `store_products` VALUES (23, 1, 28, 18.00, 100, 1);
INSERT INTO `store_products` VALUES (24, 1, 13, 8.50, 100, 1);
INSERT INTO `store_products` VALUES (25, 1, 14, 19.00, 100, 1);
INSERT INTO `store_products` VALUES (26, 1, 15, 11.50, 100, 1);
INSERT INTO `store_products` VALUES (27, 1, 29, 7.50, 100, 1);
INSERT INTO `store_products` VALUES (28, 1, 30, 12.50, 100, 1);
INSERT INTO `store_products` VALUES (29, 1, 31, 11.00, 100, 1);
INSERT INTO `store_products` VALUES (30, 1, 16, 28.00, 100, 1);
INSERT INTO `store_products` VALUES (31, 1, 17, 32.00, 100, 1);
INSERT INTO `store_products` VALUES (32, 1, 32, 19.90, 100, 1);
INSERT INTO `store_products` VALUES (33, 1, 33, 25.00, 100, 1);
INSERT INTO `store_products` VALUES (34, 1, 34, 9.90, 100, 1);
INSERT INTO `store_products` VALUES (35, 1, 35, 12.00, 100, 1);
INSERT INTO `store_products` VALUES (36, 1, 36, 13.50, 100, 1);
INSERT INTO `store_products` VALUES (37, 1, 37, 15.00, 100, 1);
INSERT INTO `store_products` VALUES (38, 1, 38, 14.50, 100, 1);
INSERT INTO `store_products` VALUES (39, 1, 39, 6.90, 100, 1);
INSERT INTO `store_products` VALUES (40, 1, 40, 12.80, 100, 1);
INSERT INTO `store_products` VALUES (41, 1, 41, 11.90, 100, 1);
INSERT INTO `store_products` VALUES (42, 1, 42, 8.90, 100, 1);
INSERT INTO `store_products` VALUES (43, 1, 43, 39.00, 100, 1);
INSERT INTO `store_products` VALUES (44, 1, 44, 15.50, 100, 1);
INSERT INTO `store_products` VALUES (45, 1, 45, 9.90, 100, 1);
INSERT INTO `store_products` VALUES (46, 1, 46, 5.90, 100, 1);
INSERT INTO `store_products` VALUES (47, 1, 47, 12.90, 100, 1);
INSERT INTO `store_products` VALUES (64, 2, 1, 28.90, 50, 1);
INSERT INTO `store_products` VALUES (65, 2, 5, 6.50, 50, 1);
INSERT INTO `store_products` VALUES (66, 2, 9, 3.50, 50, 1);
INSERT INTO `store_products` VALUES (67, 2, 13, 7.50, 50, 1);
INSERT INTO `store_products` VALUES (68, 2, 16, 19.90, 50, 1);
INSERT INTO `store_products` VALUES (69, 2, 34, 9.90, 50, 1);
INSERT INTO `store_products` VALUES (70, 2, 39, 6.90, 50, 1);
INSERT INTO `store_products` VALUES (71, 2, 44, 5.90, 50, 1);
INSERT INTO `store_products` VALUES (79, 3, 20, 45.00, 40, 1);
INSERT INTO `store_products` VALUES (80, 3, 24, 22.50, 40, 1);
INSERT INTO `store_products` VALUES (81, 3, 28, 19.50, 40, 1);
INSERT INTO `store_products` VALUES (82, 3, 31, 19.00, 40, 1);
INSERT INTO `store_products` VALUES (83, 3, 33, 32.00, 40, 1);
INSERT INTO `store_products` VALUES (84, 3, 38, 15.00, 40, 1);
INSERT INTO `store_products` VALUES (85, 3, 43, 39.00, 40, 1);
INSERT INTO `store_products` VALUES (86, 3, 47, 15.50, 40, 1);

-- ----------------------------
-- Table structure for stores
-- ----------------------------
DROP TABLE IF EXISTS `stores`;
CREATE TABLE `stores`  (
  `store_id` int(0) NOT NULL AUTO_INCREMENT,
  `store_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `latitude` decimal(10, 8) NULL DEFAULT NULL,
  `longitude` decimal(11, 8) NULL DEFAULT NULL,
  PRIMARY KEY (`store_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stores
-- ----------------------------
INSERT INTO `stores` VALUES (1, 'Market Street Flagship', '123 Market St, San Francisco, CA', 37.77490000, -122.41940000);
INSERT INTO `stores` VALUES (2, 'GreenLoop Market', 'Downtown, 5th Avenue 102', 37.78330000, -122.41670000);
INSERT INTO `stores` VALUES (3, 'Tech Park Express', 'Silicon Valley Blvd 88', 37.38810000, -122.08280000);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '新用户',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配送地址',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '15839816471', 'Lin101', 'https://ui-avatars.com/api/?name=L&background=4ADE80&color=fff&size=200', 150.00, 'Central Park West, NY 10025');
INSERT INTO `users` VALUES (2, '13800138000', 'Guest User', 'https://ui-avatars.com/api/?name=G&background=random&size=200', 0.00, NULL);

SET FOREIGN_KEY_CHECKS = 1;
