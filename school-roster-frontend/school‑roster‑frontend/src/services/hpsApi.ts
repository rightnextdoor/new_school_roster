// src/services/hpsApi.ts
import api from '../api/axios';
import { HighestPossibleScore } from '../types/HighestPossibleScore';
import { CategoryType, OperationType } from '../components/profile/enums';

/**
 * 1. POST /api/hps/view
 *    Body: { rosterId: number }
 *    Returns HighestPossibleScore
 */
export function getHps(rosterId: number) {
  return api.post<HighestPossibleScore>('/api/hps/view', { rosterId });
}

/**
 * 2. POST /api/hps/slots
 *    Body: {
 *      rosterId: number,
 *      category: CategoryType,
 *      operation: OperationType,
 *      index?: number,       // only for REMOVE
 *      maxScore?: number     // only for ADD
 *    }
 *    Returns updated HighestPossibleScore
 */
export function changeSlot(payload: {
  rosterId: number;
  category: CategoryType;
  operation: OperationType;
  index?: number;
  maxScore?: number;
}) {
  return api.post<HighestPossibleScore>('/api/hps/slots', payload);
}

/**
 * 3. POST /api/hps/ps
 *    Body: {
 *      rosterId: number,
 *      performancePs: number,
 *      quizPs: number,
 *      quarterlyExamPs: number
 *    }
 *    Returns updated HighestPossibleScore
 */
export function updatePs(payload: {
  rosterId: number;
  performancePs: number;
  quizPs: number;
  quarterlyExamPs: number;
}) {
  return api.post<HighestPossibleScore>('/api/hps/ps', payload);
}

/**
 * 4. POST /api/hps/ws
 *    Body: {
 *      rosterId: number,
 *      performanceWs: number,
 *      quizWs: number,
 *      quarterlyExamWs: number
 *    }
 *    Returns updated HighestPossibleScore
 */
export function updateWs(payload: {
  rosterId: number;
  performanceWs: number;
  quizWs: number;
  quarterlyExamWs: number;
}) {
  return api.post<HighestPossibleScore>('/api/hps/ws', payload);
}
