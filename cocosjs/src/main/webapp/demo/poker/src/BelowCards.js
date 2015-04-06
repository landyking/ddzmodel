/**
 * Created by landy on 15/3/26.
 */
var BelowCards = cc.Class.extend({
    cardNum: 3,
    node: null,

    ctor: function (parent) {
        this.node = new cc.Node();
        var contentWidth = this.cardNum * Global.unitWidth;
        this.node.setContentSize(contentWidth, Global.unitHeight);
        this.node.setPosition((cc.winSize.width - contentWidth) / 2, cc.winSize.height - Global.unitHeight);
        this.node.setVisible(false);

        parent.addChild(this.node);
    },
    showUnknowStyle: function () {
        this.node.removeAllChildren(true);
        for (var i = 0; i < this.cardNum; i++) {
            var sprite = Global.createSpriteForCard(-1);
            sprite.setPosition(Global.unitWidth / 2 + i * Global.unitWidth, Global.unitHeight / 2);
            this.node.addChild(sprite);
        }
        this.node.setVisible(true);
    },
    showCards: function (cards) {
        this.node.removeAllChildren(true);
        for (var i = 0; i < this.cardNum; i++) {
            var sprite = Global.createSpriteForCard(cards[i]);
            sprite.setPosition(Global.unitWidth / 2 + i * Global.unitWidth, Global.unitHeight / 2);
            this.node.addChild(sprite);
        }
        this.node.setVisible(true);
    },
    removeFromParent: function () {
        this.node.removeAllChildren(true);
        this.node.removeFromParent();
    }
});