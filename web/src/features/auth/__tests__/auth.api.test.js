import authApi from '../api';
import { apiClient } from '@/shared/utils/api';

describe('authApi', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.restoreAllMocks();
  });

  it('logout removes token from localStorage', () => {
    localStorage.setItem('token', 'tok');
    authApi.logout();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('login delegates to apiClient.post', async () => {
    const spy = jest.spyOn(apiClient, 'post').mockResolvedValue({ token: 'x' });
    const res = await authApi.login('a@b.com', 'pw');
    expect(spy).toHaveBeenCalledWith(expect.any(String), { email: 'a@b.com', password: 'pw' });
    expect(res).toEqual({ token: 'x' });
  });
});
