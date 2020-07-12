package com.example.scanin;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
    public String CURRENT_STATE = null;
    public static String[] STATES= {"HOME", "PDF", "CAMERA", "EDIT_1", "EDIT_2", "EDIT_3", "GRID", "ABORT"};
    Map<String, String> HOME_ACTION_NEXT = new HashMap<String, String>(){{
        put("home_open_doc", "EDIT_2");
        put("home_open_pdf", "PDF");
        put("home_add_scan", "CAMERA");
    }};

    Map<String, String> PDF_ACTION_NEXT = new HashMap<String, String>(){{
       put("edit_pdf", "EDIT_3");
       put("back", "ABORT");
    }};

    Map<String, String> CAMERA_ACTION_NEXT = new HashMap<String, String>(){{
        put("camera_capture", "EDIT_1");
        put("back", "ABORT");
    }};

    Map<String, String> EDIT1_ACTION_NEXT = new HashMap<String, String>(){{
       put("reorder", "GRID");
        put("back", "ABORT");
    }};

    Map<String, String> EDIT2_ACTION_NEXT = new HashMap<String, String>(){{
       put("reorder", "GRID");
       put("back", "CAMERA");
       put("edit_add_more", "CAMERA");
    }};

    Map<String, String> EDIT3_ACTION_NEXT = new HashMap<String, String>(){{
        put("edit_add_more", "CAMERA");
        put("back", "ABORT");
    }};

    Map<String, String> GRID_ACTION_NEXT = new HashMap<String, String>() {{
        put("back", "ABORT");
    }};

    Map<String, Map<String, String>> STATE_ACTION_NEXT = new HashMap<String, Map<String, String>>(){{
       put("HOME", HOME_ACTION_NEXT);
       put("PDF", PDF_ACTION_NEXT);
       put("CAMERA", CAMERA_ACTION_NEXT);
       put("EDIT_1", EDIT1_ACTION_NEXT);
       put("EDIT_2", EDIT2_ACTION_NEXT);
       put("EDIT_3", EDIT3_ACTION_NEXT);
       put("GRID", GRID_ACTION_NEXT);
    }};

    public String getNextState(String state, String action){
        if(state == null) state = this.CURRENT_STATE;
        Map<String, String> map = STATE_ACTION_NEXT.get(state);
        assert map != null;
        this.CURRENT_STATE = map.get(action);
        return this.CURRENT_STATE;
    }

    public void setCURRENT_STATE(String CURRENT_STATE) {
        this.CURRENT_STATE = CURRENT_STATE;
    }
}
