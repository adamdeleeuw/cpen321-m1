import express from 'express';
import { getConnectionInfo } from '../controllers/connection-info.controller';

const router = express.Router();

router.get('/', getConnectionInfo);

export default router;