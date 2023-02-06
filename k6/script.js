import http from 'k6/http';
import { check, sleep } from 'k6';

export default function () {
  const res = http.get('http://localhost:8080/delay');
  check(res, { 'status was 200': (r) => r.status == 200 });
  sleep(10);
}

export const options = {
  vus: 10000,
  preAllocatedVUs: 7500,
  maxVUs: 10000,
  duration: '20m',
  setupTimeout: '5m',
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(95)', 'p(99)', 'p(99.99)', 'count'],
};
