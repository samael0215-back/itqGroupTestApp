1. запрос:
   @Query("SELECT d FROM Document d " +
   "WHERE (:status IS NULL OR d.status = :status) " +
   "AND (:authorId IS NULL OR d.author.id = :authorId) " +
   "AND (cast (:startDate as date ) IS NULL OR d.updatedDate >= :startDate) " +
   "AND (cast(:endDate as date ) IS NULL OR d.updatedDate <= :endDate)")
   List<Document> findAllByFilters(@Param("status") Status status,
   @Param("authorId") Long authorId,
   @Param("startDate") LocalDateTime startDate,
   @Param("endDate") LocalDateTime endDate);


2. он же в SQL 
   SELECT d.*
   FROM documents d
   LEFT JOIN authors a ON d.author_id = a.id
   WHERE (:status IS NULL OR d.status = :status)
   AND (:authorId IS NULL OR d.author_id = :authorId)
   AND (CAST(:startDate AS DATE) IS NULL OR d.updated_date >= :startDate)
   AND (CAST(:endDate AS DATE) IS NULL OR d.updated_date <= :endDate);

3. Результат EXPLANE ANALIZE :
   Seq Scan on document d  (cost=0.00..28.46 rows=1000 width=66) (actual time=0.011..0.210 rows=1000.00 loops=1)
   Filter: (((status)::text = 'DRAFT'::text) AND (author_id = 1))
   Rows Removed by Filter: 31
   Buffers: shared hit=13
   Planning:
   Buffers: shared hit=42
   Planning Time: 0.175 ms
   Execution Time: 0.271 ms


Тут нету индексов так что скан всей таблицы rows=1000.00 . время выполнения малое -- тестовых данных тоже мало ..

CREATE INDEX idx_document_status_author ON document (status, author_id); (выполнено локально без миграции)

Результат после создания индекса :
Index Scan using idx_document_status_author on document d  (cost=0.29..65.34 rows=37 width=67) (actual time=0.063..0.067 rows=40.00 loops=1)
Index Cond: (((status)::text = 'APPROVED'::text) AND (author_id = 1))
Index Searches: 1
Buffers: shared hit=6
Planning Time: 0.097 ms
Execution Time: 0.082 ms

Итог:
таблица увеличена в 10раз. 
несмотря на это скорость выборки увеличилась стало -Execution Time: 0.082 ms, было -    Execution Time: 0.271 ms
использован индекс вместо прямого перебора строк стало -Index Scan using idx_document_status_author, было - eq Scan on document
использование буферов 6 вместо 13.
...
и т.д.