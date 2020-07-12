package com.example.scanin;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
    public Integer CURRENT_STATE = -1;

    Map<Integer, Integer> HOME_ACTION_NEXT = new HashMap<Integer, Integer>(){{
        put(MachineActions.HOME_OPEN_DOC, MachineStates.HOME);
        put(MachineActions.HOME_OPEN_PDF, MachineStates.PDF);
        put(MachineActions.HOME_ADD_SCAN, MachineStates.CAMERA);
    }};

    Map<Integer, Integer> PDF_ACTION_NEXT = new HashMap<Integer, Integer>(){{
       put(MachineActions.EDIT_PDF, MachineStates.EDIT_3);
       put(MachineActions.BACK, MachineStates.ABORT);
    }};

    Map<Integer, Integer> CAMERA_ACTION_NEXT = new HashMap<Integer, Integer>(){{
        put(MachineActions.CAMERA_CAPTURE_PHOTO, MachineStates.EDIT_1);
        put(MachineActions.BACK, MachineStates.ABORT);
    }};

    Map<Integer, Integer> EDIT1_ACTION_NEXT = new HashMap<Integer, Integer>(){{
       put(MachineActions.REORDER,  MachineStates.GRID);
        put(MachineActions.BACK, MachineStates.ABORT);
    }};

    Map<Integer, Integer> EDIT2_ACTION_NEXT = new HashMap<Integer, Integer>(){{
       put(MachineActions.REORDER, MachineStates.GRID);
       put(MachineActions.BACK, MachineStates.CAMERA);
       put(MachineActions.EDIT_ADD_MORE, MachineStates.CAMERA);
    }};

    Map<Integer, Integer> EDIT3_ACTION_NEXT = new HashMap<Integer, Integer>(){{
        put(MachineActions.EDIT_ADD_MORE, MachineStates.CAMERA);
        put(MachineActions.BACK, MachineStates.ABORT);
    }};

    Map<Integer, Integer> GRID_ACTION_NEXT = new HashMap<Integer, Integer>() {{
        put(MachineActions.BACK, MachineStates.ABORT);
    }};

    Map<Integer, Map<Integer, Integer>> STATE_ACTION_NEXT = new HashMap<Integer, Map<Integer, Integer>>(){{
       put(MachineStates.HOME, HOME_ACTION_NEXT);
       put(MachineStates.PDF, PDF_ACTION_NEXT);
       put(MachineStates.CAMERA, CAMERA_ACTION_NEXT);
       put(MachineStates.EDIT_1, EDIT1_ACTION_NEXT);
       put(MachineStates.EDIT_1, EDIT2_ACTION_NEXT);
       put(MachineStates.EDIT_1, EDIT3_ACTION_NEXT);
       put(MachineStates.GRID, GRID_ACTION_NEXT);
    }};

    public int getNextState(int state, int action){
        if(state == -1) state = this.CURRENT_STATE;
        Map<Integer, Integer> map = STATE_ACTION_NEXT.get(state);
        assert map != null;
        this.CURRENT_STATE = map.get(action);
        return this.CURRENT_STATE;
    }

    public void setCURRENT_STATE(int CURRENT_STATE) {
        this.CURRENT_STATE = CURRENT_STATE;
    }
}
