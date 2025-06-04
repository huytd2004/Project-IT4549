import "./minishop.css";
const MiniShop = ()=>{
   const products = [
        {
            id: 1,
            image: "/anh1.jpg",
            name: "Vòng cổ cho chó mèo kèm dây dắt AMBABY PET 1JXS041",
            price: 180000,
            reviews: 0,
        },
        {
            id: 2,
            image: "/anh2.jpg",
            name: "Yếm cho chó mèo kèm dây dắt AMBABY PET 1JXS044",
            price: 255000,
            reviews: 0,
        },
        {
            id: 3,
            image: "/anh3.jpg",
            name: "Yếm cho chó mèo kèm dây dắt AMBABY PET 1JXS054",
            price: 160000,
            reviews: 0,
        },
        {
            id: 4,
            image: "/anh4.jpg",
            name: "Mũ cho chó mèo AMBABY PET 1JXS071",
            price: 255000,
            reviews: 0,
        },
        {
            id: 5,
            image: "/anh5.jpg",
            name: "Vòng cổ chó mèo kèm dây dắt cỡ mini HAND IN HAND",
            price: 55000,
            reviews: 0,
        },
        {
            id: 6,
            image: "/anh6.jpg",
            name: "Nhà nhựa cho chó XINDING Dog House 432",
            price: 1255000,
            reviews: 0,
        },
        {
            id: 7,
            image: "/anh7.jpg",
            name: "Nhà nhựa cho chó XINDING Dog House",
            price: 255000,
            reviews: 0,
        },
        {
            id: 8,
            image: "/anh8.jpg",
            name: "Balo đựng chó mèo PETISMILE Dis78 Favorite Space",
            price: 655000,
            reviews: 0,
        },
    ];
    return (
        <>
            <div className="mini-shop">
                <h2>Sản phẩm chăm sóc thú cưng</h2>
                <div className="products-container">
                    {products.map((product) => (
                        <div className="product-card" key={product.id}>
                        <div className="product-image">
                             <img src={product.image} alt={product.name}  />
                        </div>               
                        <h4 className="product-name">{product.name}</h4>
                        <div className="product-rating">
                          <span>⭐⭐⭐⭐⭐</span> <span>({product.reviews})</span>
                        </div>
                        <p className="product-price">{product.price}đ</p>
                        <div className="product-buttons">
                          <button className="add-to-cart-btn">Thêm vào giỏ hàng</button>
                          <button className="buy-now-btn">Mua ngay</button>
                        </div>
                      </div>
                    ))}
                </div>
            </div>
        </>
    )
}
export default MiniShop;