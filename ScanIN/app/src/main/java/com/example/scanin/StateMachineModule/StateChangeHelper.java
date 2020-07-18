package com.example.scanin.StateMachineModule;

import com.example.scanin.R;
import com.example.scanin.ScanActivity;

public class StateChangeHelper {
    public static void CameraActionChange(int action, ScanActivity context){
        Integer nextState = StateMachine.getNextState(MachineStates.CAMERA, action);
        if(nextState.equals(MachineStates.EDIT_1)){
            context.imageEditFragment.setCurrentMachineState(nextState);
            context.imageEditFragment.setCurrentAdapterPosition(context.documentAndImageInfo.getImages().size()-1);
            context.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_edit, context.imageEditFragment)
                    .commit();
        }else if(nextState.equals(MachineStates.GRID_1)){
            context.imageGridFragment.setCurrentMachineState(nextState);
            context.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_grid, context.imageGridFragment)
                    .commit();
        }
    }

    public static void GridActionChange(int action, ScanActivity context, Integer pos){
        Integer nextState = StateMachine.getNextState(context.CurrentMachineState, action);
        if(context.CurrentMachineState == MachineStates.GRID_1){
            if(nextState.equals(MachineStates.CAMERA)) {
                context.getSupportFragmentManager().beginTransaction()
                        .remove(context.imageGridFragment)
                        .commit();
                context.setCamera(nextState);
            }
            else{
                context.imageEditFragment.setCurrentMachineState(nextState);
                context.imageEditFragment.setCurrentAdapterPosition(pos);
                context.getSupportFragmentManager().beginTransaction()
                        .remove(context.imageGridFragment)
                        .add(R.id.fragment_edit, context.imageEditFragment)
                        .commit();
            }
        }else if(context.CurrentMachineState == MachineStates.GRID_2){
            if(nextState.equals(MachineStates.CAMERA)) {
                context.getSupportFragmentManager().beginTransaction()
                        .remove(context.imageGridFragment)
                        .commit();
                context.startCamera();
            }
            else{
                context.imageEditFragment.setCurrentMachineState(nextState);
                context.imageEditFragment.setCurrentAdapterPosition(pos);
                context.getSupportFragmentManager().beginTransaction()
                        .remove(context.imageGridFragment)
                        .add(R.id.fragment_edit, context.imageEditFragment)
                        .commit();
            }
        }
    }

    public static void EditActionChange(int action, ScanActivity context){
        Integer nextState = StateMachine.getNextState(context.CurrentMachineState, action);
        if(context.CurrentMachineState == MachineStates.EDIT_1){
            if(nextState.equals(MachineStates.CAMERA)){
                context.getSupportFragmentManager().beginTransaction()
                        .remove(context.imageEditFragment)
                        .commit();
                context.setCamera(nextState);
            }else if(nextState.equals(MachineStates.GRID_1)){
                context.imageGridFragment.setCurrentMachineState(nextState);
                context.getSupportFragmentManager().beginTransaction()
                        .remove(context.imageEditFragment)
                        .add(R.id.fragment_grid, context.imageGridFragment)
                        .commit();
            }
        }else if(context.CurrentMachineState == MachineStates.EDIT_2){
            if(nextState.equals(MachineStates.CAMERA)){
                context.getSupportFragmentManager().beginTransaction()
                        .remove(context.imageEditFragment)
                        .commit();
                context.startCamera();
            }else if(nextState.equals(MachineStates.GRID_2)){
                context.imageGridFragment.setCurrentMachineState(nextState);
                context.getSupportFragmentManager().beginTransaction()
                        .remove(context.imageEditFragment)
                        .add(R.id.fragment_grid, context.imageGridFragment)
                        .commit();
            }
        }
    }

    public static void HomeActionChange(int action, ScanActivity context){
        int nextState = StateMachine.getNextState(context.CurrentMachineState, action);
        if(nextState == MachineStates.CAMERA){
            context.startCamera();
        }else if(nextState == MachineStates.EDIT_2){
            context.imageEditFragment.setCurrentMachineState(nextState);
            context.imageEditFragment.setCurrentAdapterPosition(0);
            context.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_edit, context.imageEditFragment)
                    .commit();
        }
    }

    public static void AnonymousActionChange(int currentState, int action, ScanActivity context){
        if(currentState == MachineStates.CAMERA){
            CameraActionChange(action, context);
        }else if(currentState == MachineStates.EDIT_1 || currentState ==MachineStates.EDIT_2){
            EditActionChange(action, context);
        }else if(currentState == MachineStates.GRID_1 || currentState == MachineStates.GRID_2){
            GridActionChange(action, context, null);
        }
    }
}
