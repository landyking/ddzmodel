var HelloWorldLayer = cc.Layer.extend({
    sprite: null,
    ctor: function () {
        //////////////////////////////
        // 1. super init first
        this._super();

        /////////////////////////////
        // 2. add a menu item with "X" image, which is clicked to quit the program
        //    you may modify it.
        // ask the window size
        var size = cc.winSize;

        var ui = ccs.load(res.Inventory_json);
        //var ui = ccs.uiReader.widgetFromJsonFile(res.InventoryItem_json);
        console.log(ui);
        var node = ui.node;
        var b19 = node.getChildByName("Button_19");
        b19.addClickEventListener(function () {
            console.log("haha Sale is clicked!");
        });
        //node.attr({x: size.width / 2, y: size.height / 2,anchorX:0.5,anchorY: 0.5});
        this.addChild(node, 100);//1c00是z轴，表示放在最上面

        return true;
    }
});

var HelloWorldScene = cc.Scene.extend({
    onEnter: function () {
        this._super();
        var layer = new HelloWorldLayer();
        this.addChild(layer);
    }
});

