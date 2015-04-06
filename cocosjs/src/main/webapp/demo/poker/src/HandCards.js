/**
 * Created by landy on 15/3/25.
 */
var HandCards = cc.Class.extend({
    cardGapSize: 15,
    picWidthCount: 13,
    picHeightCount: 5,
    spriteCards: [],
    node: null,
    needListener: false,
    location: null,
    canSee: false,

    ctor: function (parent, location, cards) {
        this.spriteCards = [];
        this.location = location;
        var node = new cc.Node();
        this.node = node;
        this.needListener = ("south" == location);
        this.canSee = ("south" == location);
        cards.sort(function (a, b) {
            return b - a;
        });
        for (var i in cards) {
            var val = cards[i];
            var sprite = Global.createSpriteForCard(val);
            sprite.setPosition(cc.p(Global.unitWidth / 2 + i * this.cardGapSize, Global.unitHeight / 2));
            var card = new Card(node, sprite, val, this.needListener);
            this.spriteCards.push(card);
        }

        this.updateNodeProperties();

        parent.addChild(this.node);
    },
    updateNodeProperties: function () {
        var hcsLength = (this.spriteCards.length - 1) * this.cardGapSize + Global.unitWidth;
        this.node.setContentSize(cc.size(hcsLength, Global.unitHeight));
        if ("south" == this.location) {
            this.node.setPosition((cc.winSize.width - hcsLength) / 2, 0);
        } else if ("east" == this.location) {
            this.node.setRotation(-90);
            this.node.setPosition(cc.winSize.width, (cc.winSize.height - hcsLength) / 2);
        } else if ("west" == this.location) {
            this.node.setRotation(90);
            this.node.setPosition(0, (cc.winSize.height + hcsLength) / 2);
        } else if ("center" == this.location) {
            this.node.setPosition((cc.winSize.width - hcsLength) / 2, (cc.winSize.height - Global.unitHeight) / 2);
        }
    },
    updateHandCardsPosition: function () {
        for (var i in this.spriteCards) {
            var cd = this.spriteCards[i];
            cd.sprite.setPosition(cc.p(Global.unitWidth / 2 + i * this.cardGapSize, Global.unitHeight / 2));
            // cd.sprite.setGlobalZOrder(cd.getCardValue());
        }
    }, /**
     * 加入指定的牌
     * @param cards
     */
    addCards: function (cards) {
        for (var i in cards) {
            var val = cards[i];
            if(!this.canSee){
                val=-1;
            }
            var sprite = Global.createSpriteForCard(val);
            var card = new Card(this.node, sprite, val, this.needListener);
            this.spriteCards.push(card);
        }
        if(this.canSee){
            this.spriteCards.sort(function (a, b) {
                return b.getCardValue() - a.getCardValue();
            });
        }
        this.updateNodeProperties();
        this.updateHandCardsPosition();

    },
    /**
     * 移除指定的牌
     * @param cards
     */
    removeCards: function (cards) {
        if (!this.canSee) {
            for (var i in cards) {
                var cd=this.spriteCards.pop();
                cd.removeFromParent();
            }
        }else{
            for (var i in cards) {
                var rd = cards[i];
                for (var y in this.spriteCards) {
                    var cd = this.spriteCards[y];
                    if (rd == cd.getCardValue()) {
                        cd.removeFromParent();
                        this.spriteCards.splice(y, 1);
                        break;
                    }
                }
            }
        }

        this.updateNodeProperties();
        this.updateHandCardsPosition();
    },
    /**
     * 清空手中所有的牌
     */
    emptyCards: function () {
        for (var y in this.spriteCards) {
            var cd = this.spriteCards[y];
            cd.removeFromParent();
            this.spriteCards[y] = null;
        }
        this.spriteCards = [];
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
        for (var i in this.spriteCards) {
            var cd = this.spriteCards[i];
            cd.removeFromParent();

            delete this.spriteCards[i];
        }
        this.spriteCards = null;
        this.node.removeFromParent();
        this.node = null;
    },
    getBoundingBox: function () {
        return this.node.getBoundingBox();
    }
});