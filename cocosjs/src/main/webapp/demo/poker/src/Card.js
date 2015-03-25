/**
 * Created by landy on 15/3/25.
 */
var Card = cc.Class.extend({
    sprite: null,
    _selected: false,
    _cardValue: null,
    touchListener: null,
    get selected() {
        return this._selected;
    },
    get cardValue() {
        return this._cardValue;
    },

    ctor: function (parent, sprite, cardValue) {
        this.sprite = sprite;
        this._cardValue = cardValue;
        var me=this;
        this.touchListener = cc.EventListener.create({
            event: cc.EventListener.TOUCH_ONE_BY_ONE,
            swallowTouches: true,
            onTouchBegan: function (touch, event) {
                var pos = touch.getLocation();
                var target = event.getCurrentTarget();
                if (cc.rectContainsPoint(target.getBoundingBoxToWorld(), pos)){
                    return true;
                }
                return false;
            },
            onTouchEnded: function (touch, event) {
                var pos = touch.getLocation();
                var target = event.getCurrentTarget();
                if (cc.rectContainsPoint(target.getBoundingBoxToWorld(), pos)) {
                    var offset=10;
                    if (me._selected) {
                        offset=-10;
                    }
                    target.runAction(cc.moveBy(0.2, 0,offset));
                    me._selected = !me._selected;
                }
            }
        });
        cc.eventManager.addListener(this.touchListener, this.sprite);

        parent.addChild(sprite);
    },
    getWidth: function () {
        this.sprite.getContentSize().width;
    },
    getHeight: function () {
        this.sprite.getContentSize().height;
    },
    setPosition: function (ps) {
        this.sprite.setPosition(ps);
    },
    removeFromParent: function () {
        cc.eventManager.removeListener(this.touchListener);
        this.touchListener = null;
        this.sprite.removeFromParent();
        this.sprite = null;
    }

});