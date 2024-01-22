LOAD CSV WITH HEADERS FROM 'file:///people.csv' AS row
FIELDTERMINATOR ';'
MERGE (p:Person {id: row.id})
	SET p.name = row.name
RETURN count(p)

LOAD CSV WITH HEADERS FROM 'file:///articles.csv' AS row
FIELDTERMINATOR ';'
MERGE (a:Article {id: row.id})
	SET a.name = row.name
RETURN count(a)

LOAD CSV WITH HEADERS FROM 'file:///buy.csv' AS row
FIELDTERMINATOR ';'
MATCH (p:Person {id: row.personId})
MATCH (a:Article {id: row.articleId})
MERGE (p)-[rel:BUY]->(a)
RETURN count(rel);
