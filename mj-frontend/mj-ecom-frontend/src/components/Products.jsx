import ProductCard from "./ProductCard.jsx";
import { FaExclamationTriangle } from "react-icons/fa";

const Products = () => {
  const isLoading = false;
  const errorMessage = "";
  const products = [
    {
      productId: 654,
      productName: "Iphone 16 Pro",
      image: "https://placehold.co/600x400",
      description: "Ultra thin mobile",
      quantity: 8,
      price: 89000,
      discount: 1000,
      specialPrice: 88000,
    },
    {
      productId: 655,
      productName: "Macbook air",
      image: "https://placehold.co/600x400",
      description: "Ultra thin laptop",
      quantity: 0,
      price: 145000,
      discount: 5000,
      specialPrice: 140000,
    },
  ];
  return (
    <div className="lg:px-14 sm:px-8 px-4 py-14 2xl:w-[90%] 2xl:mx-auto">
      {isLoading ? (
        <p>Loading...</p>
      ) : errorMessage ? (
        <div className="flex justify-center items-center h-[200px]">
          <FaExclamationTriangle className="text=slate-800 text-3xl mr-2" />
          <span className="text-slate-800 font-medium text-lg">
            {errorMessage}
          </span>
        </div>
      ) : (
        <div className="min-h-[700px]">
          <div className="pb-6 pt-14 grid 2xl:grid-cols-4 lg:ggrid-cols-3 sm:grid-cols-2 gap-y-6 gap-x-6">
            {products &&
              products.map((product, p) => (
                <ProductCard key={p} {...product} />
              ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Products;
