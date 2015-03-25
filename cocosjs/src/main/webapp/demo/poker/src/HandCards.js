/**
 * Created by landy on 15/3/25.
 */
var HandCards = cc.Node.extend({
    cardGapSize: 15,
    _cardSprites: [],

    ctor: function (parent, location, cards) {
        this._super();

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