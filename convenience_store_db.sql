/*
 Navicat Premium Data Transfer

 Source Server         : mysql8.0
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : convenience_store_db

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 14/03/2026 17:27:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `cart_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NULL DEFAULT NULL,
  `product_id` int(0) NULL DEFAULT NULL,
  `quantity` int(0) NULL DEFAULT 1,
  PRIMARY KEY (`cart_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `category_id` int(0) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, 'Fresh', 'icon_fresh.png');
INSERT INTO `categories` VALUES (2, 'Snacks', 'icon_snacks.png');
INSERT INTO `categories` VALUES (3, 'Drinks', 'icon_drinks.png');
INSERT INTO `categories` VALUES (4, 'Bakery', 'icon_bakery.png');
INSERT INTO `categories` VALUES (5, 'Desserts', 'icon_desserts.png');

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
INSERT INTO `order_items` VALUES (1, 1, 2, 1, 4.50);
INSERT INTO `order_items` VALUES (2, 1, 4, 1, 3.99);
INSERT INTO `order_items` VALUES (3, 1, 3, 2, 2.25);
INSERT INTO `order_items` VALUES (4, 2, 1, 9, 12.50);

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
  `payment_method` enum('WeChat Pay','Alipay','Apple Pay') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('pending','delivering','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending',
  `delivery_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_sn`(`order_sn`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `store_id`(`store_id`) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD-2023-084', 1, 1, 32.40, 2.00, 32.40, 'shipping', 'WeChat Pay', 'completed', 'Central Park West, NY');
INSERT INTO `orders` VALUES (2, 'ORD-2026-1F2D5245', 1, NULL, 112.50, 1.50, 114.00, 'shipping', 'WeChat Pay', 'completed', '宇宙中心大街 888');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `product_id` int(0) NOT NULL AUTO_INCREMENT,
  `category_id` int(0) NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `price` decimal(10, 2) NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '如：1L, 250g',
  `calories` int(0) NULL DEFAULT NULL,
  `protein` int(0) NULL DEFAULT NULL,
  `total_fat` int(0) NULL DEFAULT NULL,
  `shelf_life` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tag1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定义标签1',
  `tag2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定义标签2',
  `tag3` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定义标签3',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `category_id`(`category_id`) USING BTREE,
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 1, 'Zesty Avocado & Quinoa Bowl', 'A nutrient-packed powerhouse featuring organic quinoa...', 12.50, 'bowl.jpg', '1份', 420, 12, 18, '3 Days Fresh', 'Vegan', 'Gluten-Free', 'Organic');
INSERT INTO `products` VALUES (2, 3, 'Artisanal Cold Brew', '350ml Low acidity', 4.50, 'coffee.jpg', '350ml', 5, 0, 0, '5 Days', 'Low Acid', 'Sugar Free', NULL);
INSERT INTO `products` VALUES (3, 4, 'Classic Butter Croissant', 'Baked fresh today', 2.25, 'croissant.jpg', '1个', 240, 4, 12, '1 Day', 'Fresh', 'Butter', NULL);
INSERT INTO `products` VALUES (4, 2, 'Salted Kettle Chips', 'Family Pack 150g', 3.99, 'chips.jpg', '150g', 530, 2, 30, '6 Months', 'Crunchy', 'Salted', NULL);
INSERT INTO `products` VALUES (5, 1, 'Premium Sushi Platter', 'Freshly made sushi assortment featuring salmon, tuna, and avocado rolls. Served with soy sauce and wasabi.', 15.99, 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?q=80&w=800&auto=format&fit=crop', '1 Box', 520, 22, 14, '1 Day', 'Seafood', 'Fresh', 'Bestseller');
INSERT INTO `products` VALUES (6, 1, 'Grilled Chicken Caesar Salad', 'Crisp romaine lettuce, parmesan cheese, crunchy croutons, and grilled chicken breast with classic Caesar dressing.', 8.50, 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?q=80&w=800&auto=format&fit=crop', '1 Bowl', 380, 28, 16, '2 Days', 'Healthy', 'High Protein', 'Low Carb');
INSERT INTO `products` VALUES (7, 2, 'Sea Salt & Truffle Potato Chips', 'Hand-cooked potato chips flavored with real black truffle and sea salt. Perfect crunchy snack.', 4.99, 'https://images.unsplash.com/photo-1566478989037-eec170784d0b?q=80&w=800&auto=format&fit=crop', '150g', 540, 6, 32, '6 Months', 'Vegan', 'Crunchy', 'Premium');
INSERT INTO `products` VALUES (8, 2, 'Dark Chocolate Roasted Almonds', 'Premium California almonds coated in rich 70% dark chocolate. A guilt-free sweet treat.', 6.50, 'https://images.unsplash.com/photo-1621939514649-280e2af25f00?q=80&w=800&auto=format&fit=crop', '200g', 480, 12, 40, '12 Months', 'Sweet', 'Nuts', 'Antioxidants');
INSERT INTO `products` VALUES (9, 2, 'Spicy Beef Jerky', 'Hickory smoked beef jerky with a kick of black pepper and chili. High in protein.', 7.99, 'https://images.unsplash.com/photo-1599598425947-3300262939fb?q=80&w=800&auto=format&fit=crop', '100g', 280, 45, 8, '8 Months', 'Meat', 'Spicy', 'Energy');
INSERT INTO `products` VALUES (10, 3, 'Iced Matcha Latte', 'Premium Japanese matcha green tea blended with creamy oat milk and lightly sweetened.', 5.50, 'https://images.unsplash.com/photo-1536935338788-846bb9981813?q=80&w=800&auto=format&fit=crop', '500ml', 180, 4, 6, '2 Days', 'Dairy-Free', 'Energy', 'Cold');
INSERT INTO `products` VALUES (11, 3, 'Mango Passion Sparkling Water', 'Refreshing carbonated water infused with real mango and passion fruit essence. Zero sugar.', 2.99, 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?q=80&w=800&auto=format&fit=crop', '330ml', 0, 0, 0, '12 Months', 'Zero Sugar', 'Fizzy', 'Refreshing');
INSERT INTO `products` VALUES (12, 3, 'Signature Cold Brew Coffee', 'Steeped for 18 hours for a remarkably smooth, low-acid coffee experience.', 4.20, 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735?q=80&w=800&auto=format&fit=crop', '400ml', 15, 1, 0, '5 Days', 'Caffeine', 'Smooth', 'Sugar Free');
INSERT INTO `products` VALUES (13, 4, 'Classic Butter Croissant', 'Flaky, buttery, and baked fresh daily. The perfect companion for your morning coffee.', 3.50, 'https://images.unsplash.com/photo-1555507036-ab1f40ce88cb?q=80&w=800&auto=format&fit=crop', '1 Piece', 320, 6, 18, '2 Days', 'Baked Daily', 'Flaky', 'Breakfast');
INSERT INTO `products` VALUES (14, 4, 'Double Chocolate Muffin', 'Moist chocolate muffin loaded with semi-sweet chocolate chips. Decadent and rich.', 3.99, 'https://images.unsplash.com/photo-1607958996333-41aef7caefaa?q=80&w=800&auto=format&fit=crop', '1 Piece', 450, 5, 22, '3 Days', 'Sweet', 'Chocolate', 'Popular');
INSERT INTO `products` VALUES (15, 5, 'Strawberry Cheesecake', 'Classic New York style cheesecake topped with fresh strawberry glaze and a graham cracker crust.', 6.50, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?q=80&w=800&auto=format&fit=crop', '1 Slice', 480, 8, 30, '4 Days', 'Dessert', 'Sweet', 'Berry');
INSERT INTO `products` VALUES (16, 1, 'Mediterranean Quinoa Bowl', 'Nutritious quinoa base with roasted veggies, feta cheese, olives, and lemon-tahini dressing.', 9.99, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=800&auto=format&fit=crop', '1 Bowl', 420, 15, 18, '2 Days', 'Vegan', 'Gluten-Free', 'Healthy');
INSERT INTO `products` VALUES (17, 1, 'Lobster Roll', 'Succulent Maine lobster meat in a butter-toasted brioche roll with light mayo and celery.', 18.99, 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?q=80&w=800&auto=format&fit=crop', '1 Roll', 450, 24, 22, '1 Day', 'Seafood', 'Premium', 'Limited');
INSERT INTO `products` VALUES (18, 2, 'Honey Roasted Peanuts', 'Crispy peanuts coated in golden honey and a hint of sea salt. Irresistibly sweet and salty.', 3.99, 'https://images.unsplash.com/photo-1618897996116-4f44ddc50f70?q=80&w=800&auto=format&fit=crop', '250g', 580, 14, 48, '9 Months', 'Nuts', 'Sweet&Salty', 'Party');
INSERT INTO `products` VALUES (19, 2, 'Veggie Chips Mix', 'Colorful blend of beet, sweet potato, and kale chips baked to perfection. Low calorie snack.', 4.50, 'https://images.unsplash.com/photo-1541519227353-b59e29d9c979?q=80&w=800&auto=format&fit=crop', '120g', 320, 4, 18, '6 Months', 'Vegan', 'Vegetable', 'LowCal');
INSERT INTO `products` VALUES (20, 2, 'Wasabi Peas', 'Crunchy green peas coated in spicy wasabi and soy sauce. Bold flavor kick.', 3.29, 'https://images.unsplash.com/photo-1628899368756-3e6646995799?q=80&w=800&auto=format&fit=crop', '180g', 490, 10, 30, '8 Months', 'Spicy', 'Crunchy', 'Asian');
INSERT INTO `products` VALUES (21, 3, 'Cold Pressed Green Juice', '100% organic kale, spinach, cucumber, apple, and ginger. No added sugars or preservatives.', 6.99, 'https://images.unsplash.com/photo-1559329007-40df8a9345d8?q=80&w=800&auto=format&fit=crop', '350ml', 110, 2, 0, '3 Days', 'Organic', 'Healthy', 'Fresh');
INSERT INTO `products` VALUES (22, 3, 'Vanilla Protein Shake', 'Creamy vanilla whey protein shake with 25g of protein. Ready to drink, no mixing needed.', 4.99, 'https://images.unsplash.com/photo-1619359003471-4c971b9c7113?q=80&w=800&auto=format&fit=crop', '330ml', 190, 25, 3, '12 Months', 'HighProtein', 'Fitness', 'ReadyToDrink');
INSERT INTO `products` VALUES (23, 3, 'Sparkling Rose Lemonade', 'Elegant blend of rose water, fresh lemon juice, and sparkling water. Lightly sweetened.', 3.79, 'https://images.unsplash.com/photo-1595070859271-2059f2539f88?q=80&w=800&auto=format&fit=crop', '250ml', 85, 0, 0, '6 Months', 'Fruity', 'Fizzy', 'Premium');
INSERT INTO `products` VALUES (24, 4, 'Sourdough Bread Loaf', 'Artisanal sourdough bread made with natural fermentation. Crusty exterior, soft interior.', 5.99, 'https://images.unsplash.com/photo-1509440159596-0249088772ff?q=80&w=800&auto=format&fit=crop', '1 Loaf', 850, 24, 8, '5 Days', 'Artisanal', 'Fermented', 'WholeGrain');
INSERT INTO `products` VALUES (25, 4, 'Cinnamon Raisin Bagel', 'Chewy bagel with warm cinnamon and plump raisins. Perfect toasted with cream cheese.', 2.99, 'https://images.unsplash.com/photo-1628635412266-46ecb9dba999?q=80&w=800&auto=format&fit=crop', '1 Piece', 280, 7, 4, '3 Days', 'Breakfast', 'Sweet', 'Chewy');
INSERT INTO `products` VALUES (26, 4, 'Almond Croissant', 'Buttery croissant filled with almond paste and topped with sliced almonds and powdered sugar.', 4.29, 'https://images.unsplash.com/photo-1606918362486-1bc4317bf952?q=80&w=800&auto=format&fit=crop', '1 Piece', 410, 7, 26, '2 Days', 'Almond', 'Pastry', 'BakedDaily');
INSERT INTO `products` VALUES (27, 5, 'Mango Sticky Rice', 'Thai dessert with sweet glutinous rice, fresh mango, and coconut cream drizzle.', 5.99, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=800&auto=format&fit=crop', '1 Serving', 380, 4, 12, '2 Days', 'Thai', 'Fruity', 'Vegan');
INSERT INTO `products` VALUES (28, 5, 'Salted Caramel Chocolate Tart', 'Buttery shortbread crust filled with salted caramel ganache and dark chocolate shavings.', 7.29, 'https://images.unsplash.com/photo-1628899368756-3e6646995799?q=80&w=800&auto=format&fit=crop', '1 Slice', 520, 6, 32, '3 Days', 'Caramel', 'Chocolate', 'Decadent');
INSERT INTO `products` VALUES (29, 5, 'Matcha Tiramisu', 'Twist on classic tiramisu with matcha-infused mascarpone cream and ladyfingers.', 6.79, 'https://images.unsplash.com/photo-1595070859271-2059f2539f88?q=80&w=800&auto=format&fit=crop', '1 Slice', 460, 7, 28, '2 Days', 'Matcha', 'Italian', 'Creamy');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stores
-- ----------------------------
INSERT INTO `stores` VALUES (1, 'Market Street Flagship', '123 Market St, San Francisco, CA', 37.77490000, -122.41940000);
INSERT INTO `stores` VALUES (2, 'GreenLoop Market', 'Downtown, 5th Avenue 102', 37.78330000, -122.41670000);

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
INSERT INTO `users` VALUES (1, '15839816471', ',lin101', 'https://example.com/avatar1.jpg', 1240.50, '宇宙中心大街 888');
INSERT INTO `users` VALUES (2, '15839816472', 'User_6472', 'https://ui-avatars.com/api/?name=U&background=random', 0.00, NULL);

SET FOREIGN_KEY_CHECKS = 1;
