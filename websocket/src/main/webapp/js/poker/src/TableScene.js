var TableLayer = cc.Layer.extend({
    leftPlayer: null,
    rightPlayer: null,
    currentPlayer: null,
    playedCards: null,
    belowCards: null,

    ctor: function () {
        //////////////////////////////
        // 1. super init first
        this._super();

        return true;
    },
    showPoker: function (cards) {
        //var center=new HandCards("center",[0,3,8,9,13,21,22,40]);
        //this.addChild(center);


        var newVar = cards;
        //newVar.reverse();
        //console.log(newVar);
        var unknows = [];
        for (var i in cards) {
            unknows.push(-1);
        }
        var west = new Player(this, "west", unknows);
        this.leftPlayer = west;

        var south = new Player(this, "south", newVar);
        this.currentPlayer = south;

        var east = new Player(this, "east", unknows);
        this.rightPlayer = east;

        var center = new HandCards(this, "center", []);
        this.playedCards = center;

        var belowCards = new BelowCards(this);
        belowCards.showUnknowStyle();
        this.belowCards = belowCards;
    }
});

var WaitingLayer = cc.Layer.extend({
    textLabel: null,
    updateCount: 0,

    ctor: function () {
        //////////////////////////////
        // 1. super init first
        this._super();

        var size = cc.winSize;
        this.textLabel = new cc.LabelTTF("等待其他玩家", "Arial", 12);
        this.textLabel.setFontFillColor(cc.color(0, 0, 0));
        this.textLabel.setPosition(size.width / 2, size.height / 2);
        this.addChild(this.textLabel);
        this.schedule(this.updateProcessText, 1);
        return true;
    },
    updateProcessText: function () {
        this.updateCount++;
        var len = this.updateCount % 6;
        var suffix = "";
        for (var i = 0; i < len; i++) {
            suffix += ".";
        }
        this.textLabel.setString("等待其他玩家" + suffix);
    },
    clearWaitingProcess: function () {
        this.unschedule(this.updateProcessText);
        this.removeFromParent();
    }
});

var TableScene = cc.Scene.extend({
    playerId: null,
    playerPos: null,
    waitLayer: null,
    tableLayer: null,

    getPlayerByPos: function (pos) {
        if (pos >= 0) {
            if (pos == this.playerPos) {
                return this.tableLayer.currentPlayer;
            } else if ((pos + 1) % 3 == this.playerPos) {
                return this.tableLayer.leftPlayer;
            } else {
                return this.tableLayer.rightPlayer;
            }
        } else {
            throw new Error("pos " + pos);
        }
    },
    getAllPlayer:function(){
      return [this.tableLayer.leftPlayer,this.tableLayer.currentPlayer,this.tableLayer.rightPlayer];
    },
    onEnter: function () {
        this._super();
        this.addChild(new cc.LayerColor(cc.color(200, 255, 255, 180)));

        this.tableLayer = new TableLayer();
        //tableLayer.showPoker();
        this.addChild(this.tableLayer);

        this.waitingLayer = new WaitingLayer(this.tableLayer);
        this.addChild(this.waitingLayer);
        this.schedule(this.processServerResponse, 1);
        ServerRequest.AnonymousLogin(-1);
    },
    processServerResponse: function () {
        ws.doWork(this);
    }
});

