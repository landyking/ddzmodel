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
    createElementOnTable:function(){
        if(!this.leftPlayer){
            var west = new Player(this, "west");
            this.leftPlayer = west;
        }

       if(!this.currentPlayer) {
           var south = new Player(this, "south");
           this.currentPlayer = south;
       }

        if(!this.rightPlayer) {
            var east = new Player(this, "east");
            this.rightPlayer = east;
        }

        if(!this.playedCards) {
            var center = new HandCards(this, "center");
            this.playedCards = center;
        }

        if(!this.belowCards) {
            var belowCards = new BelowCards(this);
            belowCards.showUnknowStyle();
            this.belowCards = belowCards;
        }
    },
    publishCards: function (cards) {
        var unknows = [];
        for (var i in cards) {
            unknows.push(-1);
        }
        this.leftPlayer.publishCards(unknows);

        this.currentPlayer.publishCards(cards);

        this.rightPlayer.publishCards(unknows)

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
        this.setVisible(false);
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
    eachPlayer:function(func){
      var players = this.getAllPlayer();
        for(var i in players) {
            var tmp = players[i];
            if(func) {
                func(tmp);
            }
        }
    },
    onEnter: function () {
        this._super();
        this.addChild(new cc.LayerColor(cc.color(200, 255, 255, 180)));

        this.tableLayer = new TableLayer();
        //tableLayer.publishCards();
        this.addChild(this.tableLayer);

        this.waitingLayer = new WaitingLayer(this.tableLayer);
        this.addChild(this.waitingLayer);
        this.schedule(this.processServerResponse, 1);
        var pid=-1;
        this.readPlayerIdFromCookie();
        if(this.playerId) {
            pid = this.playerId;
        }
        ServerRequest.AnonymousLogin(pid);
    },
    readPlayerIdFromCookie:function(){
        //this.playerId=$.cookie('ddz.pid');
        this.playerId=Global.pid;
    },
    processServerResponse: function () {
        ws.doWork(this);
    }
});

