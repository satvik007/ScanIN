package com.example.scanin.StateMachineModule;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
    static Map<Integer, Integer> HOME_ACTION_NEXT = new HashMap<Integer, Integer>(){{
        put(MachineActions.HOME_OPEN_DOC, MachineStates.EDIT_2);
        put(MachineActions.HOME_OPEN_PDF, MachineStates.PDF);
        put(MachineActions.HOME_ADD_SCAN, MachineStates.CAMERA);
    }};

    static Map<Integer, Integer> PDF_ACTION_NEXT = new HashMap<Integer, Integer>(){{
       put(MachineActions.EDIT_PDF, MachineStates.EDIT_3);
       put(MachineActions.BACK, MachineStates.ABORT);
    }};

    static Map<Integer, Integer> CAMERA_ACTION_NEXT = new HashMap<Integer, Integer>(){{
        put(MachineActions.CAMERA_CAPTURE_PHOTO, MachineStates.EDIT_1);
        put(MachineActions.CAMERA_EDIT_GRID, MachineStates.GRID_1);
        put(MachineActions.BACK, MachineStates.ABORT);
    }};

    static Map<Integer, Integer> EDIT1_ACTION_NEXT = new HashMap<Integer, Integer>(){{
       put(MachineActions.REORDER,  MachineStates.GRID_1);
        put(MachineActions.BACK, MachineStates.CAMERA);
        put(MachineActions.EDIT_ADD_MORE, MachineStates.CAMERA);
    }};

    static Map<Integer, Integer> EDIT2_ACTION_NEXT = new HashMap<Integer, Integer>(){{
       put(MachineActions.REORDER, MachineStates.GRID_2);
        put(MachineActions.BACK, MachineStates.ABORT);
       put(MachineActions.EDIT_ADD_MORE, MachineStates.CAMERA);
    }};

//    Map<Integer, Integer> EDIT3_ACTION_NEXT = new HashMap<Integer, Integer>(){{
//        put(MachineActions.EDIT_ADD_MORE, MachineStates.CAMERA);
//        put(MachineActions.BACK, MachineStates.ABORT);
//    }};

    static Map<Integer, Integer> GRID1_ACTION_NEXT = new HashMap<Integer, Integer>() {{
        put(MachineActions.BACK, MachineStates.ABORT);
        put(MachineActions.GRID_ADD_SCAN, MachineStates.CAMERA);
        put(MachineActions.GRID_ON_CLICK, MachineStates.EDIT_1);
    }};

    static Map<Integer, Integer> GRID2_ACTION_NEXT = new HashMap<Integer, Integer>() {{
        put(MachineActions.BACK, MachineStates.ABORT);
        put(MachineActions.GRID_ADD_SCAN, MachineStates.CAMERA);
        put(MachineActions.GRID_ON_CLICK, MachineStates.EDIT_2);
    }};

    static Map<Integer, Map<Integer, Integer>> STATE_ACTION_NEXT = new HashMap<Integer, Map<Integer, Integer>>(){{
       put(MachineStates.HOME, HOME_ACTION_NEXT);
       put(MachineStates.PDF, PDF_ACTION_NEXT);
       put(MachineStates.CAMERA, CAMERA_ACTION_NEXT);
       put(MachineStates.EDIT_1, EDIT1_ACTION_NEXT);
       put(MachineStates.EDIT_2, EDIT2_ACTION_NEXT);
//       put(MachineStates.EDIT_1, EDIT3_ACTION_NEXT);
       put(MachineStates.GRID_1, GRID1_ACTION_NEXT);
        put(MachineStates.GRID_2, GRID2_ACTION_NEXT);
    }};

    public static int getNextState(int state1, int action1){
        Integer state = state1;
        Integer action = action1;
        return STATE_ACTION_NEXT.get(state).get(action);
    }
}
