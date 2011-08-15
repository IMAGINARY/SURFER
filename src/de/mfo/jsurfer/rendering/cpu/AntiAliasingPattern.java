/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering.cpu;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public enum AntiAliasingPattern implements Iterable< AntiAliasingPattern.SamplingPoint >
{
    OG_1x1( getOGSSPattern( 1 ) ),
    OG_2x2( getOGSSPattern( 2 ) ),
    OG_3x3( getOGSSPattern( 3 ) ),
    OG_4x4( getOGSSPattern( 4 ) ),
    OG_5x5( getOGSSPattern( 5 ) ),
    OG_6x6( getOGSSPattern( 6 ) ),
    OG_7x7( getOGSSPattern( 7 ) ),
    OG_8x8( getOGSSPattern( 8 ) ),
    RG_2x2( getRGSSPattern() ),
    QUINCUNX( getQuincunxPattern() );

    private final SamplingPoint[] points;

    public static class SamplingPoint
    {
        private float u, v, weight;

        private SamplingPoint( float u, float v, float weight )
        {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        public float getU() { return u;}
        public float getV() { return v; }
        public float getWeight() { return weight; }
    }

    private class SamplingPointIterator implements Iterator< SamplingPoint >
    {
        SamplingPoint[] points;
        int position;

        public SamplingPointIterator( SamplingPoint[] points )
        {
            this.points = points;
            position = 0;
        }

        public boolean hasNext()
        {
            return this.position < this.points.length;
        }

        public SamplingPoint next()
                throws NoSuchElementException
        {
            try
            {
                return points[ position++ ];
            }
            catch( ArrayIndexOutOfBoundsException e )
            {
                throw new NoSuchElementException( "No more elements" );
            }
        }

        public void remove()
                throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException( "Operation is not supported" );
        }
    }

    private AntiAliasingPattern( SamplingPoint[] points ) { this.points = points; }
    public Iterator< SamplingPoint > iterator() { return new SamplingPointIterator( this.points ); }

    private static SamplingPoint[] getOGSSPattern( int size )
    {
        assert size > 0;

        // symmetric anti aliasing patterns
        final float[][][] ogss_weights =
        {
            {
                {}
            },
            {
                {1.0f}
            },
            {
                {0.25f, 0.25f},
                {0.25f, 0.25f}
            },
            {
                {0.0625f, 0.125f, 0.0625f},
                {0.125f, 0.25f, 0.125f},
                {0.0625f, 0.125f, 0.0625f}
            },
            {
                {0.027777778f, 0.055555556f, 0.055555556f, 0.027777778f},
                {0.055555556f, 0.11111111f, 0.11111111f, 0.055555556f},
                {0.055555556f, 0.11111111f, 0.11111111f, 0.055555556f},
                {0.027777778f, 0.055555556f, 0.055555556f, 0.027777778f}
            },
            {
                {0.012345679f, 0.024691358f, 0.037037037f, 0.024691358f, 0.012345679f},
                {0.024691358f, 0.049382716f, 0.074074075f, 0.049382716f, 0.024691358f},
                {0.037037037f, 0.074074075f, 0.11111111f, 0.074074075f, 0.037037037f},
                {0.024691358f, 0.049382716f, 0.074074075f, 0.049382716f, 0.024691358f},
                {0.012345679f, 0.024691358f, 0.037037037f, 0.024691358f, 0.012345679f}
            },
            {
                {0.0069444445f, 0.013888889f, 0.020833334f, 0.020833334f, 0.013888889f, 0.0069444445f},
                {0.013888889f, 0.027777778f, 0.041666668f, 0.041666668f, 0.027777778f, 0.013888889f},
                {0.020833334f, 0.041666668f, 0.0625f, 0.0625f, 0.041666668f, 0.020833334f},
                {0.020833334f, 0.041666668f, 0.0625f, 0.0625f, 0.041666668f, 0.020833334f},
                {0.013888889f, 0.027777778f, 0.041666668f, 0.041666668f, 0.027777778f, 0.013888889f},
                {0.0069444445f, 0.013888889f, 0.020833334f, 0.020833334f, 0.013888889f, 0.0069444445f}
            },
            {
                {0.00390625f, 0.0078125f, 0.01171875f, 0.015625f, 0.01171875f, 0.0078125f, 0.00390625f},
                {0.0078125f, 0.015625f, 0.0234375f, 0.03125f, 0.0234375f, 0.015625f, 0.0078125f},
                {0.01171875f, 0.0234375f, 0.03515625f, 0.046875f, 0.03515625f, 0.0234375f, 0.01171875f},
                {0.015625f, 0.03125f, 0.046875f, 0.0625f, 0.046875f, 0.03125f, 0.015625f},
                {0.01171875f, 0.0234375f, 0.03515625f, 0.046875f, 0.03515625f, 0.0234375f, 0.01171875f},
                {0.0078125f, 0.015625f, 0.0234375f, 0.03125f, 0.0234375f, 0.015625f, 0.0078125f},
                {0.00390625f, 0.0078125f, 0.01171875f, 0.015625f, 0.01171875f, 0.0078125f, 0.00390625f}
            },
            {
                {0.0024875621f, 0.0049751243f, 0.0074626864f, 0.0099502485f, 0.0099502485f, 0.0074626864f, 0.0049751243f, 0.0024875621f},
                {0.0049751243f, 0.0099502485f, 0.014925373f, 0.019900497f, 0.019900497f, 0.014925373f, 0.0099502485f, 0.0049751243f},
                {0.0074626864f, 0.014925373f, 0.02238806f, 0.029850746f, 0.029850746f, 0.02238806f, 0.017412934f, 0.0074626864f},
                {0.0099502485f, 0.019900497f, 0.029850746f, 0.039800994f, 0.039800994f, 0.029850746f, 0.019900497f, 0.0099502485f},
                {0.0099502485f, 0.019900497f, 0.029850746f, 0.039800994f, 0.039800994f, 0.029850746f, 0.019900497f, 0.0099502485f},
                {0.0074626864f, 0.014925373f, 0.02238806f, 0.029850746f, 0.029850746f, 0.02238806f, 0.017412934f, 0.0074626864f},
                {0.0049751243f, 0.0099502485f, 0.014925373f, 0.019900497f, 0.019900497f, 0.014925373f, 0.0099502485f, 0.0049751243f},
                {0.0024875621f, 0.0049751243f, 0.0074626864f, 0.0099502485f, 0.0099502485f, 0.0074626864f, 0.0049751243f, 0.0024875621f}
            }
        };

    /*
        // same as above, but not normalized
        {
            {
                { 1 }
            },
            {
                { 1, 1 },
                { 1, 1 },
            },
            {
                {  1,  2,  1 },
                {  2,  4,  2 },
                {  1,  2,  1 }
            },
            {
                {  1,  2,  2,  1 },
                {  2,  4,  4,  2 },
                {  2,  4,  4,  2 },
                {  1,  2,  2,  1 }
            },
            {
                {  1,  2,  3,  2,  1 },
                {  2,  4,  6,  4,  2 },
                {  3,  6,  9,  6,  3 },
                {  2,  4,  6,  4,  2 },
                {  1,  2,  3,  2,  1 }
            },
            {
                {  1,  2,  3,  3,  2,  1 },
                {  2,  4,  6,  6,  4,  2 },
                {  3,  6,  9,  9,  6,  3 },
                {  3,  6,  9,  9,  6,  3 },
                {  2,  4,  6,  6,  4,  2 },
                {  1,  2,  3,  3,  2,  1 }
            },
            {
                {  1,  2,  3,  4,  3,  2,  1 },
                {  2,  4,  6,  8,  6,  4,  2 },
                {  3,  6,  9, 12,  9,  6,  3 },
                {  4,  8, 12, 16, 12,  8,  4 },
                {  3,  6,  9, 12,  9,  6,  3 },
                {  2,  4,  6,  8,  6,  4,  2 },
                {  1,  2,  3,  4,  3,  2,  1 }
            },
            {
                {  1,  2,  3,  4,  4,  3,  2,  1 },
                {  2,  4,  6,  8,  8,  6,  4,  2 },
                {  3,  6,  9, 12, 12,  9,  7,  3 },
                {  4,  8, 12, 16, 16, 12,  8,  4 },
                {  4,  8, 12, 16, 16, 12,  8,  4 },
                {  3,  6,  9, 12, 12,  9,  7,  3 },
                {  2,  4,  6,  8,  8,  6,  4,  2 },
                {  1,  2,  3,  4,  4,  3,  2,  1 }
            }
        };

     */
        SamplingPoint[] points = new SamplingPoint[ size * size ];
        try
        {
        if( size == 1 )
        {
            points[ 0 ] = new SamplingPoint( 0.5f, 0.5f, ogss_weights[ size ][ 0 ][ 0 ] );
        }
        else
        {
            float divisor = size - 1;
            for( int i = 0; i < size; ++i )
                for( int j = 0; j < size; j++ )
                    points[ i * size + j ] = new SamplingPoint( i / divisor, j / divisor, ogss_weights[ size ][ i ][ j ] );
        }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return points;
    }

    private static SamplingPoint[] getRGSSPattern()
    {
        // 4 rotated grid samples + 4 samples at pixel corners (that are usually known anyway)
        SamplingPoint[] points = new SamplingPoint[ 4 * 2 ];
        float inner_w = 0.15f;
        float outer_w = 0.25f * ( 1.0f - 4.0f * inner_w );
        points[ 0 ] = new SamplingPoint( 185.416f / 1000f, 282.652f / 1000f, inner_w );
        points[ 1 ] = new SamplingPoint( 282.62799f / 1000f, 234.047f / 1000f, inner_w );
        points[ 2 ] = new SamplingPoint( 136.813f / 1000f, 185.44099f / 1000f, inner_w );
        points[ 3 ] = new SamplingPoint( 234.026f / 1000f, 136.838f / 1000f, inner_w );
        points[ 4 ] = new SamplingPoint( 0.0f, 0.0f, outer_w );
        points[ 5 ] = new SamplingPoint( 0.0f, 1.0f, outer_w );
        points[ 6 ] = new SamplingPoint( 1.0f, 1.0f, outer_w );
        points[ 7 ] = new SamplingPoint( 1.0f, 0.0f, outer_w );
        return points;
    }

    private static SamplingPoint[] getQuincunxPattern()
    {
        /*
         * x x
         *  x
         * x x
         */
        SamplingPoint[] points = new SamplingPoint[ 5 ];
        float bw = 0.0625f + 0.25f / 3.0f;
        points[ 0 ] = new SamplingPoint( 0.0f, 0.0f, bw );
        points[ 1 ] = new SamplingPoint( 0.0f, 1.0f, bw );
        points[ 2 ] = new SamplingPoint( 1.0f, 1.0f, bw );
        points[ 3 ] = new SamplingPoint( 1.0f, 0.0f, bw );
        points[ 4 ] = new SamplingPoint( 0.5f, 0.5f, 1.0f - 4.0f * bw );
        return points;
    }
}