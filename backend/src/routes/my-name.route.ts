import express, {type Router} from 'express';
import {getMyName} from '../controllers/my-name.controller';

const router = express.Router();

router.get('/', getMyName);

export default router;