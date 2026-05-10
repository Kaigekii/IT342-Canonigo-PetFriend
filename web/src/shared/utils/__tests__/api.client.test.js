import { apiClient } from '../api';

describe('apiClient', () => {
  beforeEach(() => {
    // clear localStorage before each test
    localStorage.clear();
    jest.restoreAllMocks();
  });

  it('includes Authorization header when token present', () => {
    localStorage.setItem('token', 'abc123');
    const headers = apiClient.getHeaders({});
    expect(headers.Authorization).toBe('Bearer abc123');
    expect(headers['Content-Type']).toBe('application/json');
  });

  it('request removes token and redirects on 401', async () => {
    localStorage.setItem('token', 'secret');

    global.fetch = jest.fn().mockResolvedValue({
      status: 401,
      ok: false,
      json: async () => ({ message: 'Unauthorized' }),
    });

    await expect(apiClient.request('/some')).rejects.toThrow(/Unauthorized/);
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('request throws error with status and data on non-ok response', async () => {
    global.fetch = jest.fn().mockResolvedValue({
      status: 400,
      ok: false,
      statusText: 'Bad Request',
      json: async () => ({ message: 'Bad input' }),
    });

    await expect(apiClient.request('/x')).rejects.toMatchObject({ message: expect.any(String), status: 400 });
  });

  it('post returns parsed data on success', async () => {
    global.fetch = jest.fn().mockResolvedValue({
      status: 200,
      ok: true,
      json: async () => ({ result: 'ok' }),
    });

    const res = await apiClient.post('/p', { a: 1 });
    expect(res).toEqual({ result: 'ok' });
    expect(global.fetch).toHaveBeenCalled();
  });
});
