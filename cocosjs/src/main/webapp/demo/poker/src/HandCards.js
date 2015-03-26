/**
 * Created by landy on 15/3/25.
 */
var HandCards = cc.Class.extend({
    cardGapSize: 15,
    picWidthCount: 13,
    picHeightCount: 5,
    _cards: [],
    node: null,

    ctor: function (parent, location, cards) {

        var hcsLength = (cards.length - 1) * this.cardGapSize + Global.unitWidth;
        var node = new cc.Node();
        this.node = node;

        var needListener = false;
        node.setContentSize(cc.size(hcsLength, Global.unitHeight));

        if ("center" == location) {
            //center
            node.setPosition((cc.winSize.width - hcsLength) / 2, 0);
            needListener = true;
        } else if ("right" == location) {
            //right
            node.setRotation(-90);
            node.setPosition(cc.winSize.width, (cc.winSize.height - hcsLength) / 2);
        } else if ("left" == location) {
            //left
            node.setRotation(90);
            node.setPosition(0, (cc.winSize.height + hcsLength) / 2);
        }

        for (var i in cards) {
            var val = cards[i];
            var sprite = Global.createSpriteForCard(val);
            sprite.setPosition(cc.p(Global.unitWidth / 2 + i * this.cardGapSize, Global.unitHeight / 2));
            var card = new Card(node, sprite, val, needListener);
            this._cards.push(card);
        }

        parent.addChild(this.node);
    },
    onEnter: function () {
        this._super();
    },
    onExit: function () {
        this._super();
    },
    /**
     * 加入指定的牌
     * @param cards
     */
    addCards: function (cards) {

    },
    /**
     * 移除指定的牌
     * @param cards
     */
    removeCards: function (cards) {

    },
    /**
     * 取消选中所有牌
     */
    unSelectedAllCards: function () {

    },
    /**
     * 获取当前选中的牌列表
     */
    getSelectedCards: function () {

    },
    removeFromParent: function () {
        for (var i in this._cards) {
            var cd = this._cards[i];
            cd.removeFromParent();

            delete this._cards[i];
        }
        this._cards = null;
        this.node.removeFromParent();
        this.node = null;
    }
});