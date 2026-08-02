-- Seed data for manual/integration testing.
-- Items represent financial products (credit cards).
-- Covers: multiple reviews per item, a single review, and an item with NO reviews
-- (must resolve to average rating = 0).

INSERT INTO users (id, username, email) VALUES
	(1, 'alice', 'alice@example.com'),
	(2, 'bob', 'bob@example.com'),
	(3, 'carol', 'carol@example.com');

INSERT INTO items (id, title, description) VALUES
	(1, 'Platinum Rewards Card', 'Premium credit card with travel rewards and no foreign transaction fees'),
	(2, 'Cashback Plus Card', 'Credit card offering 2% cashback on all purchases'),
	(3, 'Student Starter Card', 'Entry-level credit card designed for building credit history'),
	(4, 'Business Elite Card', 'Credit card tailored for small business expenses and reporting'),
	(5, 'Secured Credit Card', 'Credit card backed by a refundable security deposit');

-- Platinum Rewards Card: 5, 5, 4 -> avg 4.67
INSERT INTO reviews (id, item_id, user_id, rating, comment) VALUES
	(1, 1, 1, 5, 'Excellent rewards program, very generous.'),
	(2, 1, 2, 5, 'Great travel perks and no annual fee surprises.'),
	(3, 1, 3, 4, 'Solid card, though the sign-up bonus took a while.');

-- Cashback Plus Card: 3, 2 -> avg 2.5
INSERT INTO reviews (id, item_id, user_id, rating, comment) VALUES
	(4, 2, 1, 3, 'Cashback is good but customer service is slow.'),
	(5, 2, 2, 2, 'Expected better rewards categories.');

-- Student Starter Card: 1 -> avg 1.0
INSERT INTO reviews (id, item_id, user_id, rating, comment) VALUES
	(6, 3, 3, 1, 'Low credit limit and high APR.');

-- Business Elite Card: no reviews -> avg 0 (via COALESCE)

-- Secured Credit Card: 5, 4, 3, 2 -> avg 3.5
INSERT INTO reviews (id, item_id, user_id, rating, comment) VALUES
	(7, 5, 1, 5, 'Perfect for rebuilding my credit score.'),
	(8, 5, 2, 4, 'Straightforward terms, deposit refunded on time.'),
	(9, 5, 3, 3, 'Works as expected, fees are a bit high.'),
	(10, 5, 1, 2, 'Slow to upgrade to an unsecured card.');
