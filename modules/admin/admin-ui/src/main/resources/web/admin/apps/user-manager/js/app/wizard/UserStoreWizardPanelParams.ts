import '../../api.ts';
import {UserItemWizardPanelParams} from './UserItemWizardPanelParams';
import UserStore = api.security.UserStore;
import UserStoreKey = api.security.UserStoreKey;

export class UserStoreWizardPanelParams extends UserItemWizardPanelParams<UserStore> {

}
