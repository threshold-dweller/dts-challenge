import { Application } from 'express';
import axios from 'axios';

const API_BASE = process.env.API_URL || 'http://localhost:4000';

export default function (app: Application): void {
  app.get('/', async (req, res) => {
    try {
      const response = await axios.get(`${API_BASE}/tasks`);
      res.render('home', { tasks: response.data });
    } catch (error) {
      console.error('Error fetching tasks:', error);
      res.render('home', { tasks: [], error: 'Could not load tasks. Is the backend running?' });
    }
  });
}
