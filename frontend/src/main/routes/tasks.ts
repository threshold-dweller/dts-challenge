import { Application } from 'express';
import axios from 'axios';

const API_BASE = process.env.API_URL || 'http://localhost:4000';

export default function (app: Application): void {
  app.get('/tasks/new', (req, res) => {
    res.render('tasks/new', { errors: {}, values: {} });
  });

  app.post('/tasks', async (req, res) => {
    const { title, description, status, dueDateTime } = req.body;
    try {
      await axios.post(`${API_BASE}/tasks`, { title, description, status, dueDateTime });
      res.redirect('/');
    } catch (error: unknown) {
      if (axios.isAxiosError(error) && error.response?.status === 400) {
        res.render('tasks/new', { errors: error.response.data, values: req.body });
      } else {
        res.render('tasks/new', { errors: { general: 'Failed to create task' }, values: req.body });
      }
    }
  });

  app.get('/tasks/:id', async (req, res) => {
    try {
      const response = await axios.get(`${API_BASE}/tasks/${req.params.id}`);
      res.render('tasks/view', { task: response.data });
    } catch (error: unknown) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        res.status(404).render('not-found');
      } else {
        res.redirect('/');
      }
    }
  });

  app.post('/tasks/:id/status', async (req, res) => {
    try {
      await axios.patch(`${API_BASE}/tasks/${req.params.id}/status`, { status: req.body.status });
      res.redirect(`/tasks/${req.params.id}`);
    } catch {
      res.redirect(`/tasks/${req.params.id}`);
    }
  });

  app.post('/tasks/:id/delete', async (req, res) => {
    try {
      await axios.delete(`${API_BASE}/tasks/${req.params.id}`);
    } catch (error) {
      console.error('Error deleting task:', error);
    }
    res.redirect('/');
  });
}
