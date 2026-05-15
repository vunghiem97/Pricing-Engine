import { useState } from "react";
import { calculateOrder } from "./api";

export default function App() {
    const [result, setResult] = useState<any>(null);

    const submit = async () => {
        const res = await calculateOrder({
            customerType: "VIP",
            couponCode: "SUMMER10",
            items: [
                { sku: "A", price: 100, quantity: 2 }
            ]
        });

        setResult(res);
    };

    return (
        <div>
            <h1>Pricing Engine</h1>
            <button onClick={submit}>Calculate</button>

            {result && (
                <pre>{JSON.stringify(result, null, 2)}</pre>
            )}
        </div>
    );
}