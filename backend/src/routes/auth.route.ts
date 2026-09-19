/** spec: maps POST /api/auth/google to the google sign-in controller. */
import express, {type Router} from 'express';
import {logIntoGoogle} from '../controllers/auth.controller';

const router: Router = express.Router();

router.post('/google', logIntoGoogle);

export default router;