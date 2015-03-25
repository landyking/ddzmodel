/**
 * Created by landy on 15/3/25.
 */
var HandCards = cc.Node.extend({
    cardGapSize: 15,
    picWidthCount:13,
    picHeightCount:5,
    _cards: [],

    ctor: function (location, cards) {
        this._super();
        var pokerTexture=cc.textureCache.getTextureForKey(res.poker_png);
        var pkSize = pokerTexture.getContentSize();
        var unitWidth=pkSize.width/this.picWidthCount;
        var unitHeight=pkSize.height/this.picHeightCount;
        var l=(cards.length-1)*this.cardGapSize+unitWidth;
        var s=unitHeight;

        /**
         * TODO 未知牌&大小王特殊处理
         */
        for(var i in cards) {
            var val = cards[i];
            var color=val%4;
            var cv=(val-color)/4;
            var sprite = new cc.Sprite(pokerTexture, cc.rect(cv * unitWidth, color * unitHeight, unitWidth, unitHeight));
            var card = new Card(this, sprite, val);
            this._cards.push(card);
        }

        if("center"==location){
            //center
            this.setAnchorPoint(cc.p(0.5,0.5));
            this.setContentSize(cc.size(l,s));
            for(var i in this._cards) {
                var card = this._cards[i];
                card.setPosition(cc.p(unitWidth/2+i * this.cardGapSize, unitHeight/2));
            }
            this.setPosition(cc.p(cc.winSize.width / 2, unitHeight/2));
        }else if("right"==location){
            //right
            this.setPosition(cc.p(cc.winSize.width- unitHeight/ 2, cc.winSize.height/2));
            this.setRotation(-90);
        }else if("left"==location){
            //left
            this.setAnchorPoint(cc.p(0.5,0.5));
            this.setContentSize(cc.size(s,l));
            for(var i in this._cards) {
                var card = this._cards[i];
                card.setVertical(false);
                card.sprite.setRotation(90);
                card.setPosition(cc.p(unitHeight/2,unitWidth/2+i * this.cardGapSize));
            }
            this.setPosition(cc.p(unitHeight/2,cc.winSize.height / 2));
        }
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

    }
});