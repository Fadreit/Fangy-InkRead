import request from './request'
import type { LoginPayload, LoginResponse, PasswordPayload, RegisterPayload, User } from '@/types/api'

const asData = <T>(promise: Promise<unknown>) => promise as Promise<T>

export const authApi = {
  register(payload: RegisterPayload) {
    return asData<User>(request.post('/users/register', payload))
  },
  login(payload: LoginPayload) {
    return asData<LoginResponse>(request.post('/users/login', payload))
  },
  profile() {
    return asData<User>(request.get('/users/profile'))
  },
  updateProfile(payload: Partial<User>) {
    return asData<User>(request.put('/users/profile', payload))
  },
  updatePassword(payload: PasswordPayload) {
    return asData<string>(request.put('/users/password', payload))
  }
}
