import { Environment } from '../enums/environment';
import type { ServiceMode } from '../enums/service_mode';
import type { Theme } from './theme';

export interface DigioConfig {
  logo?: string;
  environment?: Environment;
  theme?: Theme;
  serviceMode?: ServiceMode;
}
