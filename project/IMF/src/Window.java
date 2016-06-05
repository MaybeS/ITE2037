
import imf.data.DataObject;
import imf.data.DataParser;
import imf.object.*;
import imf.processor.Keyboard;
import imf.processor.Mouse;
import imf.processor.ProcessManager;
import imf.processor.Scene;
import imf.utility.Pair;
import imf.processor.IProcessProperty;
import imf.processor.Interaction;
import imf.processor.Physics;
import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.TextBox;
import loot.graphics.Viewport;

public class Window extends GameFrame
{
	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 2015004584L;

	private enum STATE{
		splash, cancel, loading, play, over
	}
	
	boolean reload = true;
	static STATE state = STATE.splash;
	
	Constant path;
	DataParser data;
	
	Viewport viewport;
	TextBox text;
	
	/**
	 * processor(ProcessManager) manage processors.
	 * 
	 * @extends: IProcess
	 */
	ProcessManager processor;
	Interaction interaction;
	Keyboard keyboard;
	Physics physics;
	Mouse mouse;
	Scene scene;
	
	ObjectManager<SpriteObject> sprites;
	ObjectManager<ContainerObject> containers;
	CharacterObject me = null, you;
	SpriteObject stage;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
		
		// load settings
		path = new Constant(settings);
		
		// create new instance
		sprites = new ObjectManager<SpriteObject>();
		containers = new ObjectManager<ContainerObject>();
		processor = new ProcessManager(new Stage());
		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		text = new TextBox();
	}
	
	@Override
	public boolean Initialize() 
	{
		switch (state)
		{
			// load data
			case splash:
				data = new DataParser(path.MAP + "splash.xml", 0);
				break;
			case cancel:
				containers.get("start").invisible(false);
				me.pos_y = 50;
				break;
			case loading:
				containers.get("start").invisible(true);
				((TriggerObject) containers.get("loading")).invisible(false);
				((TriggerObject) containers.get("loadAni")).trigger();
				break;
			case play:
				data = new DataParser(path.MAP + "stage1.xml", 0);
				break;
			case over:
				break;
		}
		
		if (!reload)
			return true;
		
		data.loop((e)->{
			if(e.ID.equals("me") && me == null)
			{
				me = new CharacterObject(e);
				images.LoadImage(path.RES + me.texture, "me");
				me.image = images.GetImage("me");
				viewport.children.add(me);
				me.a_y = -0.98;
			}
			else
				newObject(e);
		});
		
		// install processor
		processor.install("interaction", interaction = new Interaction(me));
		processor.install("keyboard", keyboard = new Keyboard(inputs));
		processor.install("physics", physics = new Physics(me));
		processor.install("scene", scene = new Scene(viewport, stage = newObject(data.stage)));
		processor.install("mouse", mouse = new Mouse(inputs, settings.canvas_width, settings.canvas_height));
		processor.initilize(processor);
		
		// install objects
		sprites.loop((e)->{
			install(e);
		});
		
		containers.loop((e)->{
			install(e);
		});
		
		// scene setting
		scene.setter(me);

		// text (for debug)
		text.height = 50;
		text.width = 50;
		text.x = 0;
		text.y = 0;
		
		// viewport setting
		viewport.radius_x = 25;
		viewport.radius_y = 25;
		viewport.pointOfView_z = 10000;
		viewport.view_baseDistance = 10000;
		viewport.view_minDistance = 1000;
		viewport.view_maxDistance = 100000;
		viewport.view_width = settings.canvas_width;
		viewport.view_height = settings.canvas_height;
		
		if(me != null)
			viewport.children.add(me);
		viewport.children.add(text);
		
		reload = false;
		return true;
	}
	
	public void Destroy()
	{
		// create new instance
		sprites = new ObjectManager<SpriteObject>();
		containers = new ObjectManager<ContainerObject>();
		processor = new ProcessManager(new Stage());
		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		text = new TextBox();
		Initialize();
	}
	
	@Override
	public boolean Update(long timeStamp) 
	{
		inputs.AcceptInputs();
		switch (state)
		{
			case splash:
				break;
			case loading:
				break;
			case play:
				text.text = "" + me.a_y;
				break;
			case over:
				break;
		}
		processor.loop();
		return true;
	}

	@Override
	public void Draw(long timeStamp) 
	{
		switch (state)
		{
			case splash:
				break;
			case loading:
				break;
			case play:
				break;
			case over:
				break;
		}
		BeginDraw();
		ClearScreen();
		viewport.Draw(g);
		EndDraw();
	}
	
	public class Stage implements IProcessProperty<String, Integer> {
		@Override
		public void setter(String object) {
			switch (object) {
				case "start":
					state = STATE.loading;
					Initialize();
					break;
				case "stop":
					state = STATE.cancel;
					Initialize();
					break;
			}
		}
		@Override
		public Integer getter() {
			return state.ordinal();
		}
	}
	
	private void install(SpriteObject o)
	{
		images.LoadImage(path.RES + o.texture, o.texture);
		o.image = images.GetImage(o.texture);
		physics.install(o);
		viewport.children.add(o);
		if(o.type.equals("button"))
			mouse.install(o);
	}
	
	private void uninstall(SpriteObject o)
	{
		physics.uninstall(o);
		viewport.children.remove(o);
		sprites.remove(o);
		if (o.ID.equals("container"))
		{
			ContainerObject c = (ContainerObject) o;
			containers.remove(c);
			for (SpriteObject s : c.childs)
				uninstall(s);
		}
		if(o.type.equals("button"))
			mouse.uninstall(o);
	}
	
	private SpriteObject newObject(DataObject e)
	{
		SpriteObject ret = null;
		switch (e.ID)
		{
			case "stage":
				ret = new SpriteObject(e);
				install(ret);
				break;
			case "static":
				ret = new SpriteObject(e);
				sprites.insert(e.get("name"), ret);
				break;
			case "container":
				switch (e.get("type"))
				{
					case "box":
						ret = new ContainerObject(e);
						containers.insert(e.get("name"), (ContainerObject) ret);
					case "button":
					case "trigger":
						ret = new TriggerObject(e);
						containers.insert(e.get("name"), (TriggerObject) ret);
				}
				for (DataObject o : e.getChild())
					((ContainerObject) ret).add(newObject(o));
				break;
		}
		if (e.get("visible").equals("false"))
			ret.invisible(true);
		return ret;
	}
}
