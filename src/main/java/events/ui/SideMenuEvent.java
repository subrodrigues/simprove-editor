/*
 * Created by Filipe André Rodrigues on 28-05-2019 23:47
 */

/*
 * Created by Filipe André Rodrigues on 28-05-2019 23:03
 */

package events.ui;

import base.BaseEvent;
import base.ResponseError;
import utils.ConstantUtils.SideMenuOption;

public class SideMenuEvent extends BaseEvent {
    private SideMenuOption mSideMenuOption;

    public SideMenuEvent(SideMenuOption menuOption) {
        this.mSideMenuOption = menuOption;
    }

    public SideMenuEvent(ResponseError error) {
        super(error);
    }

    public SideMenuOption getSideMenuOption() {
        return mSideMenuOption;
    }

    public void setSideMenuOption(SideMenuOption mSideMenuOption) {
        this.mSideMenuOption = mSideMenuOption;
    }
}
