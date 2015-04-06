var TableLayer = cc.Layer.extend({
    ctor: function () {
        //////////////////////////////
        // 1. super init first
        this._super();

        /////////////////////////////
        // 2. add a menu item with "X" image, which is clicked to quit the program
        //    you may modify it.
        // ask the window size
        var size = cc.winSize;

        //this.showPoker(size);
        //this.testMenu(size);


        /*cc.eventManager.addListener(cc.EventListener.create({
         event: cc.EventListener.TOUCH_ONE_BY_ONE,
         //swallowTouches: true,
         onTouchBegan: function (touch, event) {
         var pos = touch.getLocation();
         var target = event.getCurrentTarget();
         console.log(pos);
         return true;
         }
         }), this);*/

        return true;
    },
    showPoker: function () {
        //var center=new HandCards("center",[0,3,8,9,13,21,22,40]);
        //this.addChild(center);


        var newVar = [0, 3, 8, 9, 10, 12, 13, 18, 19, 21, 22, 32, 40];
        //newVar.reverse();
        console.log(newVar);
        var unknows = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1];
        var west = new Player(this, "west", unknows);

        var south = new Player(this, "south", newVar);
        window.south=south;

        var east = new Player(this, "east", unknows);
        window.east=east;

        var center = new HandCards(this, "center", [3,5,7]);
        window.center=center;

        var belowCards = new BelowCards(this);
        belowCards.showUnknowStyle();

        window.belowCards = belowCards;
    }
});

var WaitingLayer=cc.Layer.extend({
    textLabel:null,
    updateCount:0,
    tableLayer:null,

    ctor: function (tableLayer) {
        //////////////////////////////
        // 1. super init first
        this._super();

        this.tableLayer=tableLayer;

        var size = cc.winSize;
        this.textLabel = new cc.LabelTTF("等待其他玩家","Arial", 12);
        this.textLabel.setFontFillColor(cc.color(0, 0, 0));
        this.textLabel.setPosition(size.width / 2, size.height/2);
        this.addChild(this.textLabel);
        this.schedule(this.updateText, 1);
        return true;
    },
    updateText:function(){
        this.updateCount++;
        var len=this.updateCount%6;
        var suffix="";
        for(var i=0;i<len;i++) {
            suffix += ".";
        }
        this.textLabel.setString("等待其他玩家"+suffix);
        if(this.updateCount>10) {
            this.startPublishCards();
        }
    },
    startPublishCards:function(){
        this.unschedule(this.updateText);
        this.removeFromParent();
        this.tableLayer.showPoker();
    }
});

var TableScene = cc.Scene.extend({
    onEnter: function () {
        this._super();
        this.addChild(new cc.LayerColor(cc.color(200, 255, 255, 180)));

        var tableLayer = new TableLayer();
        //tableLayer.showPoker();
        this.addChild(tableLayer);
        var timerLayer = new TimerLayer();
        window.timeLayer = timerLayer;
        this.addChild(timerLayer);

        this.addChild(new WaitingLayer(tableLayer));
    }
});

