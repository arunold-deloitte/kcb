export interface TestResult {
  email: string;
  firstName: string;
  lastName: string;
  lob: string;
  score: number;
}

export const TEST_RESULTS: TestResult[] = [
  { email: 'user1@example.com', firstName: 'John', lastName: 'Doe', lob: 'ALM', score: 85 },
  { email: 'user2@example.com', firstName: 'Jane', lastName: 'Smith', lob: 'HP', score: 92 },
  { email: 'user3@example.com', firstName: 'Peter', lastName: 'Jones', lob: 'HOM', score: 78 },
  { email: 'user4@example.com', firstName: 'Susan', lastName: 'Williams', lob: 'SAM / CLH', score: 88 },
  { email: 'user5@example.com', firstName: 'David', lastName: 'Brown', lob: 'R&R', score: 95 },
  { email: 'user6@example.com', firstName: 'Mary', lastName: 'Davis', lob: 'NPI', score: 82 },
  { email: 'user7@example.com', firstName: 'James', lastName: 'Miller', lob: 'GPN', score: 90 },
  { email: 'user8@example.com', firstName: 'Patricia', lastName: 'Wilson', lob: 'Invoice', score: 75 },
  { email: 'user9@example.com', firstName: 'Robert', lastName: 'Moore', lob: 'VMO - M&A', score: 89 },
  { email: 'user10@example.com', firstName: 'Jennifer', lastName: 'Taylor', lob: 'Supplier Catalog', score: 93 },
];
