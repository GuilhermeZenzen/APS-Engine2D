package com.aps.engine;

public class GameController implements Runnable {
	private Thread thread;
	private Window window;
	private Renderer renderer;
	private static Input input;
	private AbstractGame game;
	
	private boolean isRunning = false;
	private final double UPDATE_CAP = 1.0 / 60.0;
	private static float deltaTime = 0;
	private static float timeScale = 1f;
	private static int width = 320, height = 480;
	private static float scale = 2f;
	private static String title = "APS Engine v1.0";
	
	public GameController(AbstractGame game) {
		this.game = game;
	}
	
	public void start() {
		Window.setWindow();
		Renderer.setRenderer();
		input = new Input(this);
		
		thread = new Thread(this);
		
		while (!thread.isAlive()) {
			thread.run();
			isRunning = true;
		}
	}
	
	public void stop() {
		
	}
	
	public void run() {
		isRunning = true;
		boolean render = false;
		double firstTime = 0;
		double lastTime = System.nanoTime() / 1000000000.0;
		double passedTime = 0;
		double unprocessedTime = 0;
		
		double frameTime = 0;
		int frames = 0;
		int fps = 0;
		
		while(isRunning) {
			render = false;
			
			firstTime = System.nanoTime() / 1000000000.0;
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			
			unprocessedTime += passedTime;
			frameTime += passedTime;
			
			while(unprocessedTime >= UPDATE_CAP) {
				deltaTime = (float)unprocessedTime;
				unprocessedTime -= UPDATE_CAP;
				render = true;
				
				game.update(this, deltaTime);
				Input.update();
							
				if(frameTime >= 1.0) {
					frameTime = 0;
					fps = frames;
					frames = 0;
				}
			}
			
			if(render) {
				game.render(this, renderer);
				Renderer.scalePixels();
				Renderer.drawText("FPS: " + fps, 0, 0, 0, 0, 300, 30, 0xffffff00, 3);
				Window.update();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		dispose();
	}
	
	private void dispose() {
		
	}
	
	public Window getWindow() {
		return window;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public static Input getInput() {
		return input;
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int newWidth) {
		width = newWidth;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int newHeight) {
		height = newHeight;
	}

	public static float getScale() {
		return scale;
	}

	public static void setScale(float newScale) {
		scale = newScale;
	}

	public static int getScaledWidth() {
		return (int)(width * scale);
	}
	public static int getScaledHeight() {
		return (int)(height * scale);
	}
	
	public static String getTitle() {
		return title;
	}

	public static void setTitle(String newTitle) {
		title = newTitle;
	}
	
	public static float getDeltaTime() {
		return deltaTime;
	}

	public static float getTimeScale() {
		return timeScale;
	}

	public static void setTimeScale(float timeScale) {
		GameController.timeScale = timeScale;
	}
}
