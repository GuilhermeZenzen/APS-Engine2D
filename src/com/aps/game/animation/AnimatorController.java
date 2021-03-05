package com.aps.game.animation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.aps.engine.gfx.Image;
import com.aps.game.GameManager;
import com.aps.game.Tileset;

public class AnimatorController {
	private String tag;
	
	private ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	
	private ArrayList<State> states = new ArrayList<State>();
	private State defaultState;
	private State anyState;
	
	public AnimatorController(String tag) {
		this.tag = tag;
		anyState = new State("any-state", null, 0);
	}
	public AnimatorController(String tag, ArrayList<State> states, State defaultState, ArrayList<Parameter> parameters) {
		this.tag = tag;
		anyState = new State("any-state", null, 0);
	}
	
	public static AnimatorController newController(String tag, String animatorPattern) {
		int lineCount = 1;
		
		try {
			InputStream file = GameManager.class.getResourceAsStream("/animator-controllers/" + animatorPattern + ".txt");
			if (file == null) return null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(file));
			
			String line = reader.readLine();
			AnimatorController animatorController = new AnimatorController(tag);
			Map<String, Animation> currentAnimations = null;
			Transition currentTransition = null;
			
			while (line != null) {
				String objects[] = line.split("/", 0);
				switch (objects[0]) {
				case "Animations":
					currentAnimations = AnimationList.ANIMATIONS.get(objects[1]);
					break;
				case "Parameter":
					Parameter parameter = null;
					switch (objects[2]) {
					case "bool":
						parameter = new BooleanParameter(objects[1], objects[3].contentEquals("true"));
						break;
					case "trigger":
						parameter = new TriggerParameter(objects[1]);
						break;
					case "int":
						parameter = new IntegerParameter(objects[1], Integer.parseInt(objects[3]));
						break;
					case "float":
						parameter = new FloatParameter(objects[1], Float.parseFloat(objects[3]));
					}
					if (parameter != null) {
						animatorController.addParameter(parameter);
					}
					break;
				case "State":
					State state = new State(objects[1], currentAnimations.get(objects[2]), Float.parseFloat(objects[3]));
					
					if (state != null) {
						animatorController.addState(state);
					}
					break;
				case "Default":
					animatorController.setDefaultState(objects[1]);
					break;
				case "Transition":
					currentTransition = new Transition(objects[1], animatorController.getState(objects[3]), objects[4].contentEquals("true"), Float.parseFloat(objects[5]), objects[6].contentEquals("true"));
					
					if (currentTransition != null) {
						if (objects[2].contentEquals("any-state")) {
							animatorController.anyState.addTransition(currentTransition);
						} else {
							animatorController.getState(objects[2]).addTransition(currentTransition);
						}
					}
					break;
				case "Condition":
					Condition condition = null;
					Parameter param = animatorController.findParameter(objects[1]);
					if (param != null) {
						if (param instanceof BooleanParameter) {
							condition = new BooleanCondition((BooleanParameter)param, objects[2].contentEquals("true"));
						} else if (param instanceof TriggerParameter) {
							
						} else if (param instanceof IntegerParameter) {
							IntegerComparasion intComparation = IntegerComparasion.SMALLER;
							switch (objects[2]) {
							case "<":
								intComparation = IntegerComparasion.SMALLER;
								break;
							case "<=":
								intComparation = IntegerComparasion.SMALLER_EQUAL;
								break;
							case "=":
								intComparation = IntegerComparasion.EQUAL;
								break;
							case ">=":
								intComparation = IntegerComparasion.BIGGER_EQUAL;
								break;
							case ">":
								intComparation = IntegerComparasion.BIGGER;
							}
							condition = new IntegerCondition((IntegerParameter)param, intComparation, Integer.parseInt(objects[3]));
						} else {
							FloatComparasion floatComparation = FloatComparasion.SMALLER;
							if (objects[2].contentEquals("<")) {
								floatComparation = FloatComparasion.SMALLER;
							} else {
								floatComparation = FloatComparasion.BIGGER;
							}
							condition = new FloatCondition((FloatParameter)param, floatComparation, Float.parseFloat(objects[3]));
						}
						
						if (currentTransition != null && condition != null) {
							currentTransition.addCondition(condition);
						}
					}
					break;
				}

				line = reader.readLine();
				lineCount++;
			}

			reader.close();
			return animatorController;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro na linha " + lineCount + ".");
			return null;
		}
	}
	
	public Parameter findParameter(String tag) {
		for (Parameter parameter : parameters) {
			if (parameter.getTag().contentEquals(tag)) {
				return parameter;
			}
		}
		return null;
	}
	private void addParameter(Parameter parameter) {
		int paramIndex = parameters.indexOf(findParameter(parameter.getTag()));
		
		if (paramIndex < 0) {
			parameters.add(parameter);
		} else {
			parameters.set(paramIndex, parameter);
		}
	}

	public BooleanParameter findBool(String tag) {
		for (Parameter parameter : parameters) {
			if (parameter instanceof BooleanParameter) {
				if (parameter.getTag().contentEquals(tag)) {
					return (BooleanParameter)parameter;
				}
			}
		}
		return null;
	}
	public void addBool(String tag, boolean value) {
		BooleanParameter parameter = findBool(tag);
		
		if (parameter == null) {
			parameters.add(new BooleanParameter(tag, value));
		} else {
			parameter.setValue(value);
		}
	}
	
	public IntegerParameter findInt(String tag) {
		for (Parameter parameter : parameters) {
			if (parameter instanceof IntegerParameter) {
				if (parameter.getTag().contentEquals(tag)) {
					return (IntegerParameter)parameter;
				}
			}
		}
		return null;
	}
	public void addInt(String tag, int value) {
		IntegerParameter parameter = findInt(tag);
		
		if (parameter == null) {
			parameters.add(new IntegerParameter(tag, value));
		} else {
			parameter.setValue(value);
		}
	}
	
	public FloatParameter findFloat(String tag) {
		for (Parameter parameter : parameters) {
			if (parameter instanceof FloatParameter) {
				if (parameter.getTag().contentEquals(tag)) {
					return (FloatParameter)parameter;
				}
			}
		}
		return null;
	}
	public void addFloat(String tag, float value) {
		FloatParameter parameter = findFloat(tag);
		
		if (parameter == null) {
			parameters.add(new FloatParameter(tag, value));
		} else {
			parameter.setValue(value);
		}
	}
	
	public void addState(State state) {
		states.add(state);
	}
	public State getState(String tag) {
		for (State state : states) {
			if (state.getTag().contentEquals(tag)) {
				return state;
			}
		}
		
		return null;
	}
	public ArrayList<State> getStates() {
		return states;
	}
	
	public State getDefaultState() {
		return defaultState;
	}
	public void setDefaultState(int index) {
		if (index < states.size()) {
			defaultState = states.get(index);
		}
	}
	public void setDefaultState(String tag) {
		State state = getState(tag);
		if (state != null) {
			defaultState = state;
		}
	}

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}
	public State getAnyState() {
		return anyState;
	}
}
